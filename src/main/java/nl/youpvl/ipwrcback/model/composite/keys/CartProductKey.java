package nl.youpvl.ipwrcback.model.composite.keys;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CartProductKey implements Serializable {
    private Long cart;
    private Long product;
}
