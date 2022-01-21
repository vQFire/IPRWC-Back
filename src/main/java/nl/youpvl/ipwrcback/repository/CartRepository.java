package nl.youpvl.ipwrcback.repository;

import nl.youpvl.ipwrcback.model.Cart;
import nl.youpvl.ipwrcback.model.CartProduct;
import nl.youpvl.ipwrcback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser (User user);
}