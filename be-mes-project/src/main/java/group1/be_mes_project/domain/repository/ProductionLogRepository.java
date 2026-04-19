package group1.be_mes_project.domain.repository;

import group1.be_mes_project.domain.entity.ProductionLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductionLogRepository extends JpaRepository<ProductionLog, Long> {

  interface WorkOrderLoadedCountView {
    String getWoId();

    long getLoadedCount();
  }

  List<ProductionLog> findByWorkOrder_WoIdOrderByTimestampAsc(String woId);

  Optional<ProductionLog> findFirstByWorkOrder_WoIdOrderByTimestampAsc(String woId);

  Optional<ProductionLog> findFirstByWorkOrder_WoIdOrderByTimestampDesc(String woId);

  long countByWorkOrder_WoId(String woId);

  @Query("""
      select pl.workOrder.woId as woId, count(pl) as loadedCount
      from ProductionLog pl
      group by pl.workOrder.woId
      """)
  List<WorkOrderLoadedCountView> countLoadedByWorkOrder();
}

