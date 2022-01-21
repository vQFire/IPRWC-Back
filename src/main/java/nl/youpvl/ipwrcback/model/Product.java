package nl.youpvl.ipwrcback.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Double price;

    @Lob
    @Column(columnDefinition = "text")
    private String shortDescription;

    @Lob
    @Column(columnDefinition = "text")
    private String longDescription;
    private Boolean sale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(getId(), product.getId()) && Objects.equals(getName(), product.getName()) && Objects.equals(getPrice(), product.getPrice()) && Objects.equals(getShortDescription(), product.getShortDescription()) && Objects.equals(getLongDescription(), product.getLongDescription()) && Objects.equals(getSale(), product.getSale());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getPrice(), getShortDescription(), getLongDescription(), getSale());
    }
}
