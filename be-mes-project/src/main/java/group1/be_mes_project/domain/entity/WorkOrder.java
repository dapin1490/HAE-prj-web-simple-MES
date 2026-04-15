package group1.be_mes_project.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "WorkOrders")
public class WorkOrder {

  @Id
  @Column(name = "wo_id", nullable = false, length = 100)
  private String woId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private SalesOrder salesOrder;

  @Column(name = "planned_qty", nullable = false)
  private Double plannedQty;

  @Column(name = "machine_id")
  private String machineId;

  protected WorkOrder() {}

  public WorkOrder(String woId, SalesOrder salesOrder, Double plannedQty, String machineId) {
    this.woId = woId;
    this.salesOrder = salesOrder;
    this.plannedQty = plannedQty;
    this.machineId = machineId;
  }

  public String getWoId() {
    return woId;
  }

  public SalesOrder getSalesOrder() {
    return salesOrder;
  }

  public Double getPlannedQty() {
    return plannedQty;
  }

  public String getMachineId() {
    return machineId;
  }
}

