package group1.be_mes_project.config;

import group1.be_mes_project.domain.entity.Product;
import group1.be_mes_project.domain.entity.SalesOrder;
import group1.be_mes_project.domain.repository.ProductRepository;
import group1.be_mes_project.domain.repository.SalesOrderRepository;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PlanningSeedData implements CommandLineRunner {

  private final ProductRepository productRepository;
  private final SalesOrderRepository salesOrderRepository;
  private final String datasourceUrl;

  public PlanningSeedData(
      ProductRepository productRepository,
      SalesOrderRepository salesOrderRepository,
      @Value("${spring.datasource.url:}") String datasourceUrl) {
    this.productRepository = productRepository;
    this.salesOrderRepository = salesOrderRepository;
    this.datasourceUrl = datasourceUrl;
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (datasourceUrl == null || !datasourceUrl.startsWith("jdbc:h2:")) {
      return;
    }

    if (productRepository.count() > 0 || salesOrderRepository.count() > 0) {
      return;
    }

    Product product1 =
        productRepository.save(new Product("P-1001", "High Stretch Poly Fabric", "Dyeing", 120));
    Product product2 =
        productRepository.save(new Product("P-1002", "Cotton Blend Fabric", "Dyeing", 150));

    salesOrderRepository.save(
        new SalesOrder("SO-20220101-001", product1, LocalDate.of(2022, 1, 1), 400.0));
    salesOrderRepository.save(
        new SalesOrder("SO-20220102-001", product1, LocalDate.of(2022, 1, 2), 350.0));
    salesOrderRepository.save(
        new SalesOrder("SO-20220101-002", product2, LocalDate.of(2022, 1, 1), 500.0));
  }
}

