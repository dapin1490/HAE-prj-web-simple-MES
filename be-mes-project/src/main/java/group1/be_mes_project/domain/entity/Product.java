package group1.be_mes_project.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Products")
public class Product {

  @Id
  @Column(name = "product_id", nullable = false, length = 100)
  private String productId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "category")
  private String category;

  @Column(name = "safety_stock")
  private Integer safetyStock;

  protected Product() {}

  public Product(String productId, String name, String category, Integer safetyStock) {
    this.productId = productId;
    this.name = name;
    this.category = category;
    this.safetyStock = safetyStock;
  }

  public String getProductId() {
    return productId;
  }

  public String getName() {
    return name;
  }

  public String getCategory() {
    return category;
  }

  public Integer getSafetyStock() {
    return safetyStock;
  }
}

