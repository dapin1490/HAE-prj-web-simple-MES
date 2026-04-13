import { computed, ref } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client/dist/sockjs'

const DEFAULT_MAX_REALTIME_MESSAGE_COUNT = 50

function getHttpOriginFromApiBaseUrl() {
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
  if (typeof apiBaseUrl !== 'string' || apiBaseUrl.trim() === '') {
    return 'http://localhost:8080'
  }

  try {
    const parsedUrl = new URL(apiBaseUrl)
    return `${parsedUrl.protocol}//${parsedUrl.host}`
  } catch {
    return 'http://localhost:8080'
  }
}

function getWebSocketEndpoint() {
  const configuredEndpoint = import.meta.env.VITE_WS_ENDPOINT
  if (typeof configuredEndpoint === 'string' && configuredEndpoint.trim() !== '') {
    return configuredEndpoint
  }
  return `${getHttpOriginFromApiBaseUrl()}/ws-mes`
}

function getProductionTrendTopic() {
  const configuredTopic = import.meta.env.VITE_WS_TOPIC
  if (typeof configuredTopic === 'string' && configuredTopic.trim() !== '') {
    return configuredTopic
  }
  return '/topic/production-trend'
}

export function useProductionTrendSocket() {
  const connectionState = ref('disconnected')
  const lastErrorMessage = ref('')
  const lastReceivedMessage = ref(null)
  const receivedMessageList = ref([])
  let stompClient = null
  let topicSubscription = null

  const isConnected = computed(() => connectionState.value === 'connected')

  function disconnect() {
    if (!stompClient) {
      connectionState.value = 'disconnected'
      return
    }

    if (topicSubscription) {
      topicSubscription.unsubscribe()
      topicSubscription = null
    }

    const clientToClose = stompClient
    stompClient = null
    connectionState.value = 'disconnecting'
    void clientToClose.deactivate().finally(() => {
      connectionState.value = 'disconnected'
    })
  }

  function connect() {
    if (stompClient || connectionState.value === 'connecting') {
      return
    }

    lastErrorMessage.value = ''
    connectionState.value = 'connecting'

    const nextClient = new Client({
      reconnectDelay: 0,
      webSocketFactory: () => new SockJS(getWebSocketEndpoint()),
      onConnect: () => {
        connectionState.value = 'connected'
        topicSubscription = nextClient.subscribe(getProductionTrendTopic(), (message) => {
          let parsedMessage
          try {
            parsedMessage = JSON.parse(message.body)
          } catch {
            parsedMessage = message.body
          }

          lastReceivedMessage.value = parsedMessage
          receivedMessageList.value = [parsedMessage, ...receivedMessageList.value].slice(
            0,
            DEFAULT_MAX_REALTIME_MESSAGE_COUNT,
          )
        })
      },
      onStompError: (frame) => {
        lastErrorMessage.value = frame.headers.message ?? 'STOMP 오류가 발생했습니다.'
      },
      onWebSocketError: () => {
        lastErrorMessage.value = 'WebSocket 연결 오류가 발생했습니다.'
      },
      onWebSocketClose: () => {
        if (connectionState.value !== 'disconnected') {
          connectionState.value = 'disconnected'
        }
      },
    })

    stompClient = nextClient
    stompClient.activate()
  }

  function clearReceivedMessages() {
    receivedMessageList.value = []
  }

  return {
    connectionState,
    isConnected,
    lastErrorMessage,
    lastReceivedMessage,
    receivedMessageList,
    connect,
    disconnect,
    clearReceivedMessages,
  }
}
