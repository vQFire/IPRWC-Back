package nl.youpvl.ipwrcback.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<ProductPurchase> productPurchases = new HashSet<>();

    private Double totalPrice = 0.0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void addProduct (Product product, Integer amount) {
        productPurchases.add(new ProductPurchase(null, this, product, amount));
    }
}
