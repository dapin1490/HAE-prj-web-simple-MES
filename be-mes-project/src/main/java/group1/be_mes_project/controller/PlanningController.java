package group1.be_mes_project.controller;

import group1.be_mes_project.dto.ApiResponse;
import group1.be_mes_project.dto.planning.ProductDto;
import group1.be_mes_project.dto.planning.SalesOrderDto;
import group1.be_mes_project.service.PlanningService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PlanningController {

  private final PlanningService planningService;

  public PlanningController(PlanningService planningService) {
    this.planningService = planningService;
  }

  @GetMapping("/products")
  public ApiResponse<List<ProductDto>> getProducts() {
    return ApiResponse.success(planningService.getProducts());
  }

  @GetMapping("/orders")
  public ApiResponse<List<SalesOrderDto>> getOrders() {
    return ApiResponse.success(planningService.getOrders());
  }

  @GetMapping("/orders/{product_id}")
  public ApiResponse<List<SalesOrderDto>> getOrdersByProductId(
      @PathVariable("product_id") String productId) {
    return ApiResponse.success(planningService.getOrdersByProductId(productId));
  }
}

