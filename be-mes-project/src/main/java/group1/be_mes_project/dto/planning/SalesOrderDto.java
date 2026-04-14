package group1.be_mes_project.dto.planning;

import java.time.LocalDate;

public record SalesOrderDto(
    String orderId,
    String productId,
    LocalDate orderDate,
    Double orderQty) {}

