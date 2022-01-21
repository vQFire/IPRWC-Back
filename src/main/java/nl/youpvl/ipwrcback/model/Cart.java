package nl.youpvl.ipwrcback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<CartProduct> products;

    public Cart() {
        products = new ArrayList<>();
    }

    public void addToCart (Product product) {
        Optional<CartProduct> optionalCartProduct = findProductInCart(product);

        if (optionalCartProduct.isPresent()) {
            CartProduct cartProduct = optionalCartProduct.get();

            cartProduct.setAmount(cartProduct.getAmount() + 1);
        } else {
            CartProduct cartProduct = new CartProduct(this, product, 1);
            products.add(cartProduct);
        }
    }

    public void removeFromCart (Product product) {
        Optional<CartProduct> optionalCartProduct = findProductInCart(product);

        if (optionalCartProduct.isPresent()) {
            CartProduct cartProduct = optionalCartProduct.get();

            cartProduct.setAmount(cartProduct.getAmount() - 1);

            if (cartProduct.getAmount() == 0) {
                products.remove(cartProduct);
            }
        }
    }

    private Optional<CartProduct> findProductInCart (Product product) {
        return products.stream().filter(cartProduct -> cartProduct.getProduct().equals(product)).findFirst();
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }
}
