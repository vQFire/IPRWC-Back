package nl.youpvl.ipwrcback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.youpvl.ipwrcback.model.composite.keys.CartProductKey;

import javax.persistence.*;

@Entity
@IdClass(CartProductKey.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartProduct {
    @Id
    @ManyToOne
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    @JsonIgnore
    private Cart cart;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    private Integer amount;
}
