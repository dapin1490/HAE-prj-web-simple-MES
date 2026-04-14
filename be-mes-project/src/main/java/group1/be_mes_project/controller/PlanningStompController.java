package group1.be_mes_project.controller;

import group1.be_mes_project.dto.ApiResponse;
import group1.be_mes_project.dto.planning.PlanningOrderFilterDto;
import group1.be_mes_project.dto.planning.ProductDto;
import group1.be_mes_project.dto.planning.SalesOrderDto;
import group1.be_mes_project.service.PlanningService;
import java.util.List;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class PlanningStompController {

  private final PlanningService planningService;

  public PlanningStompController(PlanningService planningService) {
    this.planningService = planningService;
  }

  @MessageMapping("/planning/products")
  @SendToUser("/queue/planning/products")
  public ApiResponse<List<ProductDto>> getProducts() {
    return ApiResponse.success(planningService.getProducts());
  }

  @MessageMapping("/planning/orders")
  @SendToUser("/queue/planning/orders")
  public ApiResponse<List<SalesOrderDto>> getOrders() {
    return ApiResponse.success(planningService.getOrders());
  }

  @MessageMapping("/planning/orders/by-product")
  @SendToUser("/queue/planning/orders/by-product")
  public ApiResponse<List<SalesOrderDto>> getOrdersByProductId(PlanningOrderFilterDto filter) {
    if (filter == null || filter.productId() == null || filter.productId().isBlank()) {
      return ApiResponse.fail("product_id is required");
    }
    return ApiResponse.success(planningService.getOrdersByProductId(filter.productId()));
  }

  @MessageExceptionHandler(Exception.class)
  @SendToUser("/queue/errors")
  public ApiResponse<Void> handleWebSocketException(Exception exception) {
    return ApiResponse.fail("Unexpected error: " + exception.getMessage());
  }
}
