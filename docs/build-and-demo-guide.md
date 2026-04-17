# 빌드 및 발표용 PC 시연 가이드

이 문서는 서버 배포 없이 발표용 PC에서 시연하기 위한 빌드, 파일 이동, 실행 절차를 정리한다.

## 1. 목적에 따른 시연 방식

### A. 프론트 단독 시연 (픽스처 데이터)
- 목적: 네트워크나 백엔드 상태와 무관하게 화면 시연
- 특징: 백엔드 실행 없음, 정적 파일 서버만 실행

### B. 풀스택 시연 (프론트 + 백엔드)
- 목적: 실제 WebSocket 연동 동작 시연
- 특징: 발표용 PC에서 Java로 백엔드 JAR 실행 + 프론트 정적 서버 실행

## 2. 사전 준비

- 개발 PC
  - Node.js 20.19 이상 또는 22.12 이상
  - Java 17 이상
- 발표용 PC
  - 픽스처 시연: Python 또는 Node.js 중 1개
  - 풀스택 시연: Java 17 이상 + Python 또는 Node.js

## 3. 개발 PC에서 패키지 생성

저장소 루트에서 아래 배치 파일을 실행한다.

```bat
scripts\build-presentation-package.bat
```

이 스크립트는 `data\backend_ready`의 CSV를 `be-mes-project\src\main\resources\data`로 자동 복사한 뒤 빌드한다.  
동일 파일명이 이미 있으면 덮어쓴다.
필수 CSV 5종(`Products.csv`, `SalesOrders.csv`, `WorkOrders.csv`, `ProductionLogs.csv`, `Inspections.csv`)이 하나라도 없으면 즉시 실패한다.

완료되면 `demo-package` 폴더가 생성된다.

- `demo-package\frontend-dist`: 프론트 빌드 결과물
- `demo-package\backend\*.jar`: 백엔드 실행 파일
- `demo-package\run-demo-fixture.bat`: 픽스처 시연 실행 스크립트
- `demo-package\run-demo-fullstack.bat`: 풀스택 시연 실행 스크립트

## 4. 발표용 PC로 파일 이동

`demo-package` 폴더 전체를 USB 또는 네트워크 공유로 발표용 PC에 복사한다.

## 5. 발표용 PC에서 실행

### 5.1 프론트 단독(픽스처) 시연

`demo-package\run-demo-fixture.bat` 실행 후 브라우저에서 아래 주소로 접속한다.

- [http://localhost:4173](http://localhost:4173)

### 5.2 풀스택 시연

`demo-package\run-demo-fullstack.bat` 실행 후 브라우저에서 아래 주소로 접속한다.

- [http://localhost:4173](http://localhost:4173)

실행 시 별도 콘솔 2개가 열린다.
- 백엔드 콘솔: Java JAR 실행
- 프론트 콘솔: 정적 파일 서버 실행

## 6. 발표 직전 점검

- `http://localhost:4173` 접속 확인
- 풀스택 시연 시 백엔드 콘솔에서 예외 로그 여부 확인
- 방화벽 또는 보안 프로그램이 로컬 포트(4173, 8080)를 차단하지 않는지 확인

## 7. 문제 발생 시 빠른 확인

- Java 미설치: `run-demo-fullstack.bat` 실행 시 Java 설치 안내 메시지 출력
- Python/Node 미설치: 정적 서버 실행 실패 메시지 출력
- JAR 또는 dist 누락: 먼저 `scripts\build-presentation-package.bat` 재실행
