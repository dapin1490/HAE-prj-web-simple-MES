package group1.be_mes_project.service.impl;

import group1.be_mes_project.domain.entity.Product;
import group1.be_mes_project.domain.entity.SalesOrder;
import group1.be_mes_project.domain.repository.ProductRepository;
import group1.be_mes_project.domain.repository.SalesOrderRepository;
import group1.be_mes_project.dto.planning.ProductDto;
import group1.be_mes_project.dto.planning.SalesOrderDto;
import group1.be_mes_project.service.PlanningService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PlanningServiceImpl implements PlanningService {

  private final ProductRepository productRepository;
  private final SalesOrderRepository salesOrderRepository;

  public PlanningServiceImpl(
      ProductRepository productRepository, SalesOrderRepository salesOrderRepository) {
    this.productRepository = productRepository;
    this.salesOrderRepository = salesOrderRepository;
  }

  @Override
  public List<ProductDto> getProducts() {
    return productRepository.findAll().stream().map(this::toProductDto).toList();
  }

  @Override
  public List<SalesOrderDto> getOrders() {
    return salesOrderRepository.findAllByOrderByOrderDateAsc().stream().map(this::toSalesOrderDto).toList();
  }

  @Override
  public List<SalesOrderDto> getOrdersByProductId(String productId) {
    return salesOrderRepository.findByProduct_ProductIdOrderByOrderDateAsc(productId).stream()
        .map(this::toSalesOrderDto)
        .toList();
  }

  private ProductDto toProductDto(Product product) {
    return new ProductDto(
        product.getProductId(), product.getName(), product.getCategory(), product.getSafetyStock());
  }

  private SalesOrderDto toSalesOrderDto(SalesOrder salesOrder) {
    return new SalesOrderDto(
        salesOrder.getOrderId(),
        salesOrder.getProduct().getProductId(),
        salesOrder.getOrderDate(),
        salesOrder.getOrderQty());
  }
}

