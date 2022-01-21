package nl.youpvl.ipwrcback.repository;

import nl.youpvl.ipwrcback.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserUsername (String username);
}
