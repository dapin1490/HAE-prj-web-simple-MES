package group1.be_mes_project.simulation;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SimulationTimelineReference {

  private static final DateTimeFormatter CSV_TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final String sourcePath;
  private final Map<String, Long> totalDurationSecondsByWo = new HashMap<>();
  private final Map<String, Integer> totalRowsByWo = new HashMap<>();

  public SimulationTimelineReference(@Value("${mes.simulation.source-path:}") String sourcePath) {
    this.sourcePath = sourcePath;
  }

  @PostConstruct
  void init() {
    loadReference();
  }

  public long getTotalDurationSeconds(String woId) {
    return totalDurationSecondsByWo.getOrDefault(woId, 0L);
  }

  public int getTotalRows(String woId) {
    return totalRowsByWo.getOrDefault(woId, 0);
  }

  public Map<String, Long> snapshotDurations() {
    return Collections.unmodifiableMap(totalDurationSecondsByWo);
  }

  private void loadReference() {
    totalDurationSecondsByWo.clear();
    totalRowsByWo.clear();

    String effectivePath =
        (sourcePath == null || sourcePath.isBlank()) ? "data/ProductionLogs.csv" : sourcePath;

    Map<String, LocalDateTime> minTimestampByWo = new HashMap<>();
    Map<String, LocalDateTime> maxTimestampByWo = new HashMap<>();

    try (InputStream inputStream =
            Thread.currentThread().getContextClassLoader().getResourceAsStream(effectivePath);
        BufferedReader reader =
            inputStream == null
                ? null
                : new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

      if (reader == null) {
        return;
      }

      String headerLine = reader.readLine();
      if (headerLine == null) {
        return;
      }

      Map<String, Integer> headerMap = buildHeaderMap(headerLine);
      String line;
      while ((line = reader.readLine()) != null) {
        String[] columns = line.split(",", -1);
        String woId = getColumn(columns, headerMap, "wo_id");
        LocalDateTime timestamp = parseDateTime(getColumn(columns, headerMap, "timestamp"));

        if (woId == null || timestamp == null) {
          continue;
        }

        totalRowsByWo.merge(woId, 1, Integer::sum);
        minTimestampByWo.merge(woId, timestamp, (a, b) -> a.isBefore(b) ? a : b);
        maxTimestampByWo.merge(woId, timestamp, (a, b) -> a.isAfter(b) ? a : b);
      }

      for (Map.Entry<String, LocalDateTime> entry : minTimestampByWo.entrySet()) {
        String woId = entry.getKey();
        LocalDateTime min = entry.getValue();
        LocalDateTime max = maxTimestampByWo.get(woId);
        if (max == null) {
          continue;
        }
        long seconds = Math.max(0L, Duration.between(min, max).getSeconds());
        totalDurationSecondsByWo.put(woId, seconds);
      }
    } catch (Exception ignored) {
      totalDurationSecondsByWo.clear();
      totalRowsByWo.clear();
    }
  }

  private Map<String, Integer> buildHeaderMap(String headerLine) {
    String[] headers = headerLine.split(",", -1);
    Map<String, Integer> headerMap = new HashMap<>();
    for (int index = 0; index < headers.length; index++) {
      headerMap.put(headers[index].trim().toLowerCase(), index);
    }
    return headerMap;
  }

  private String getColumn(String[] columns, Map<String, Integer> headerMap, String key) {
    Integer index = headerMap.get(key.toLowerCase());
    if (index == null || index < 0 || index >= columns.length) {
      return null;
    }
    String value = columns[index].trim();
    return value.isEmpty() ? null : value;
  }

  private LocalDateTime parseDateTime(String value) {
    if (value == null) {
      return null;
    }
    try {
      return LocalDateTime.parse(value, CSV_TIMESTAMP_FORMATTER);
    } catch (Exception exception) {
      try {
        return LocalDateTime.parse(value);
      } catch (Exception ignored) {
        return null;
      }
    }
  }
}

