package group1.be_mes_project.dto.quality;

import group1.be_mes_project.dto.execution.ProductionLogDto;
import group1.be_mes_project.dto.execution.WorkOrderDto;
import java.util.List;

public record ReportDto(
    WorkOrderDto workOrder,
    List<ProductionLogDto> productionLogs,
    InspectionDto inspection) {}
