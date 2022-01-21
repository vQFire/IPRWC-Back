package nl.youpvl.ipwrcback.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.youpvl.ipwrcback.model.Product;
import nl.youpvl.ipwrcback.model.Purchase;
import nl.youpvl.ipwrcback.model.User;
import nl.youpvl.ipwrcback.model.request.NewPurchase;
import nl.youpvl.ipwrcback.repository.ProductRepository;
import nl.youpvl.ipwrcback.repository.PurchaseRepository;
import nl.youpvl.ipwrcback.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/purchase")
public class PurchaseController {
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    @GetMapping()
    public ResponseEntity<List<Purchase>> getAllPurchases () {
        log.info("Fetching all the purchases");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            return ResponseEntity.ok(this.purchaseRepository.findByUserUsername(authentication.getName()));
        }
        
        return ResponseEntity.ok(this.purchaseRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchase (@PathVariable Long id) {
        Optional<Purchase> optionalPurchase = this.purchaseRepository.findById(id);
        
        if (optionalPurchase.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(optionalPurchase.get());
    }

    @PostMapping("/create")
    public ResponseEntity<Purchase> createPurchase (@RequestBody NewPurchase newPurchase) {
        List<Long> ids = new ArrayList<>();

        for (Map<String, Long> purchaseItem: newPurchase.products) {
            ids.add(purchaseItem.get("id"));
        }

        List<Product> products = this.productRepository.findByIds(ids);

        if (products.size() == 0) return ResponseEntity.notFound().build();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = this.userRepository.findByUsername(username).get();

        Purchase purchase = new Purchase();
        purchase.setUser(user);

        for (int x = 0; x < products.size(); x++) {
            Product product = products.get(x);
            Integer amount = newPurchase.products.get(x).get("amount").intValue();

            purchase.setTotalPrice(purchase.getTotalPrice() + product.getPrice() * amount);
            purchase.addProduct(product, amount);
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/purchase/create").toUriString());

        return ResponseEntity.created(uri).body(this.purchaseRepository.save(purchase));
    }
}
