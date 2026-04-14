package group1.be_mes_project.domain.repository;

import group1.be_mes_project.domain.entity.SalesOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, String> {

  List<SalesOrder> findAllByOrderByOrderDateAsc();

  List<SalesOrder> findByProduct_ProductIdOrderByOrderDateAsc(String productId);
}

