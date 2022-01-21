package nl.youpvl.ipwrcback.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.youpvl.ipwrcback.model.Product;
import nl.youpvl.ipwrcback.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/product")
@CrossOrigin("http://localhost:4200/**")
public class ProductController {
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts (@RequestParam Map<String, String> filters) {
        List<Product> products = this.productRepository.findAll();

        if (filters.containsKey("name")) {
            String searchName = filters.get("name").toLowerCase();
            products = products.stream().filter(product -> product.getName().toLowerCase().contains(searchName))
                    .collect(Collectors.toList());
        }

        if (filters.containsKey("max_price")) {
            Double maxPrice = Double.valueOf(filters.get("max_price"));
            products = products.stream().filter(product -> product.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }

        if (filters.containsKey("min_price")) {
            Double maxPrice = Double.valueOf(filters.get("min_price"));
            products = products.stream().filter(product -> product.getPrice() >= maxPrice)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Product> getProduct (@PathVariable("name") String name) {
        Optional<Product> optionalProduct = this.productRepository.findByName(name);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(optionalProduct.get());
    }

    @PostMapping("/create")
    public ResponseEntity<Product> saveProduct (@RequestBody Product product) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/product/create").toUriString());

        return ResponseEntity.created(uri).body(this.productRepository.save(product));
    }

    @PutMapping("/update")
    public ResponseEntity<Product> updateProduct (@RequestBody Product product) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/product/update").toUriString());
        Optional<Product> optionalProduct = this.productRepository.findById(product.getId());

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.created(uri).body(this.productRepository.save(product));
    }

    @DeleteMapping("/{name}/delete")
    public ResponseEntity<?> deleteProduct (@PathVariable("name") String name) {
        Optional<Product> optionalProduct = this.productRepository.findByName(name);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        this.productRepository.delete(optionalProduct.get());

        log.info("Removing product {}", optionalProduct.get().getName());

        return ResponseEntity.ok().build();
    }
}
