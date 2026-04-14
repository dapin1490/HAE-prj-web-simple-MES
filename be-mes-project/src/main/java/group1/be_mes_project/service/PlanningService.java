package group1.be_mes_project.service;

import group1.be_mes_project.dto.planning.ProductDto;
import group1.be_mes_project.dto.planning.SalesOrderDto;
import java.util.List;

public interface PlanningService {

  List<ProductDto> getProducts();

  List<SalesOrderDto> getOrders();

  List<SalesOrderDto> getOrdersByProductId(String productId);
}

