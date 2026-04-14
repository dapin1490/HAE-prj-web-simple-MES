package group1.be_mes_project.dto.execution;

import java.util.List;

public record ProductionProgressDto(Double progress, List<WorkOrderProgressDto> workOrders) {}

