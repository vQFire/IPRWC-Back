package nl.youpvl.ipwrcback.repository;

import nl.youpvl.ipwrcback.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName (String name);

    @Query("SELECT p FROM Product p WHERE p.id in :ids")
    List<Product> findByIds (List<Long> ids);
    List<Product> findByNameContains (String name);
}
