package group1.be_mes_project.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "SalesOrders")
public class SalesOrder {

  @Id
  @Column(name = "order_id", nullable = false, length = 100)
  private String orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "order_date", nullable = false)
  private LocalDate orderDate;

  @Column(name = "order_qty", nullable = false)
  private Double orderQty;

  protected SalesOrder() {}

  public SalesOrder(String orderId, Product product, LocalDate orderDate, Double orderQty) {
    this.orderId = orderId;
    this.product = product;
    this.orderDate = orderDate;
    this.orderQty = orderQty;
  }

  public String getOrderId() {
    return orderId;
  }

  public Product getProduct() {
    return product;
  }

  public LocalDate getOrderDate() {
    return orderDate;
  }

  public Double getOrderQty() {
    return orderQty;
  }
}

