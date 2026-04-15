package group1.be_mes_project.domain.repository;

import group1.be_mes_project.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {}

