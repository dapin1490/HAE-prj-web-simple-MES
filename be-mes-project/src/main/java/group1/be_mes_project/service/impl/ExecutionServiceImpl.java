package group1.be_mes_project.service.impl;

import group1.be_mes_project.domain.entity.ProductionLog;
import group1.be_mes_project.domain.entity.WorkOrder;
import group1.be_mes_project.domain.repository.ProductionLogRepository;
import group1.be_mes_project.domain.repository.WorkOrderRepository;
import group1.be_mes_project.dto.execution.ProductionLogDto;
import group1.be_mes_project.dto.execution.ProductionProgressDto;
import group1.be_mes_project.dto.execution.WorkOrderDto;
import group1.be_mes_project.dto.execution.WorkOrderProgressDto;
import group1.be_mes_project.service.ExecutionService;
import group1.be_mes_project.simulation.SimulationTimelineReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExecutionServiceImpl implements ExecutionService {

  private final WorkOrderRepository workOrderRepository;
  private final ProductionLogRepository productionLogRepository;
  private final SimulationTimelineReference timelineReference;

  public ExecutionServiceImpl(
      WorkOrderRepository workOrderRepository,
      ProductionLogRepository productionLogRepository,
      SimulationTimelineReference timelineReference) {
    this.workOrderRepository = workOrderRepository;
    this.productionLogRepository = productionLogRepository;
    this.timelineReference = timelineReference;
  }

  @Override
  public List<WorkOrderDto> getWorkOrders() {
    return workOrderRepository.findAllByOrderByWoIdAsc().stream().map(this::toWorkOrderDto).toList();
  }

  @Override
  public List<ProductionLogDto> getProductionLogsByWoId(String woId) {
    return productionLogRepository.findByWorkOrder_WoIdOrderByTimestampAsc(woId).stream()
        .map(this::toProductionLogDto)
        .toList();
  }

  @Override
  public ProductionProgressDto getProductionProgress() {
    List<WorkOrder> workOrders = workOrderRepository.findAllByOrderByWoIdAsc();
    List<WorkOrderProgressDto> workOrderProgresses = new ArrayList<>(workOrders.size());
    Map<String, Long> loadedCountByWoId = loadCountByWoId();

    double weightedProgressSum = 0.0;
    double totalPlannedQty = 0.0;

    for (WorkOrder workOrder : workOrders) {
      String woId = workOrder.getWoId();
      long loadedCount = loadedCountByWoId.getOrDefault(woId, 0L);
      double progressRatio = calculateProgressRatio(woId, loadedCount);
      workOrderProgresses.add(
          new WorkOrderProgressDto(woId, round(progressRatio * 100.0)));

      Double plannedQty = workOrder.getPlannedQty();
      if (plannedQty != null && plannedQty > 0.0) {
        weightedProgressSum += plannedQty * progressRatio;
        totalPlannedQty += plannedQty;
      }
    }

    double overallProgress =
        totalPlannedQty <= 0.0 ? 0.0 : round((weightedProgressSum / totalPlannedQty) * 100.0);

    return new ProductionProgressDto(overallProgress, workOrderProgresses);
  }

  private WorkOrderDto toWorkOrderDto(WorkOrder workOrder) {
    return new WorkOrderDto(
        workOrder.getWoId(),
        workOrder.getSalesOrder().getOrderId(),
        workOrder.getPlannedQty(),
        workOrder.getMachineId());
  }

  private ProductionLogDto toProductionLogDto(ProductionLog log) {
    return new ProductionLogDto(
        log.getLogId(),
        log.getWorkOrder().getWoId(),
        log.getTimestamp(),
        log.getCrTemp(),
        log.getTempSp(),
        log.getTempPv(),
        log.getSpeed());
  }

  private Map<String, Long> loadCountByWoId() {
    Map<String, Long> loadedCountByWoId = new HashMap<>();
    for (ProductionLogRepository.WorkOrderLoadedCountView view :
        productionLogRepository.countLoadedByWorkOrder()) {
      if (view.getWoId() != null) {
        loadedCountByWoId.put(view.getWoId(), view.getLoadedCount());
      }
    }
    return loadedCountByWoId;
  }

  private double calculateProgressRatio(String woId, long loadedCount) {
    if (woId == null) {
      return 0.0;
    }

    int totalRows = timelineReference.getTotalRows(woId);
    if (totalRows <= 0) {
      return 0.0;
    }

    return Math.max(0.0, Math.min(1.0, loadedCount / (double) totalRows));
  }

  private double round(double value) {
    return Math.round(value * 10.0) / 10.0;
  }
}

