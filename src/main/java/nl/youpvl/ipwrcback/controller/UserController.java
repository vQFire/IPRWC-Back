package nl.youpvl.ipwrcback.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.youpvl.ipwrcback.model.Cart;
import nl.youpvl.ipwrcback.model.Product;
import nl.youpvl.ipwrcback.model.Role;
import nl.youpvl.ipwrcback.model.User;
import nl.youpvl.ipwrcback.model.request.GrantRoleToUser;
import nl.youpvl.ipwrcback.model.request.UpdateUser;
import nl.youpvl.ipwrcback.repository.ProductRepository;
import nl.youpvl.ipwrcback.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
@CrossOrigin("http://localhost:4200")
public class UserController {
    private final UserService userService;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<User>> getUsers () {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<User> saveUser (@ModelAttribute User user) {
        user = userService.saveUser(user);

        userService.grantPermissions(user.getUsername(), "ROLE_USER");

        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<User> profile () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateUser (@RequestBody UpdateUser user) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        loggedInUser.setEmail(user.getEmail());
        loggedInUser.setName(user.getName());

        loggedInUser = this.userService.saveUser(loggedInUser);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user").toUriString());
        return ResponseEntity.created(uri).body(loggedInUser);
    }

    @PutMapping("/profile/{name}")
    public ResponseEntity<User> updateUser (@RequestBody UpdateUser user, @PathVariable("name") String name) {
        Optional<User> optionalUser = userService.getUser(name);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User originalUser = optionalUser.get();

        originalUser.setEmail(user.getEmail());
        originalUser.setName(user.getName());

        originalUser = this.userService.saveUser(originalUser);

        return ResponseEntity.ok(originalUser);
    }

    @GetMapping("/{name}")
    public ResponseEntity<User> getUser (@PathVariable("name") String name) {
        Optional<User> optionalUser = userService.getUser(name);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user").toUriString());
        return ResponseEntity.created(uri).body(optionalUser.get());
    }

    @PostMapping("/grant")
    public ResponseEntity<?> grantPermissionToUser (@RequestBody GrantRoleToUser grantRoleToUser) {
        userService.grantPermissions(grantRoleToUser.getUsername(), grantRoleToUser.getRoleName());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/refresh")
    public void refreshToken (HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("IkHebNogNooitZoIetsVeiligsGezienDAMN".getBytes());

                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);

                Optional<User> optionalUser = userService.getUser(decodedJWT.getSubject());

                if (optionalUser.isEmpty()) {
                    throw new RuntimeException("Subject does not exist");
                }

                User user = optionalUser.get();

                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", token);

                response.setContentType(APPLICATION_JSON_VALUE);

                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                log.error("Something went wrong while logging in. {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(403);

                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }

        throw new RuntimeException("Refresh token is missing");
    }

    @GetMapping("/cart")
    public ResponseEntity<Cart> getCart () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(user.getCart());
    }

    @GetMapping("/cart/{id}")
    public ResponseEntity<Cart> addToCard (@PathVariable("id") Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart = user.getCart();

        cart.addToCart(optionalProduct.get());

        user = userService.saveUser(user);

        return ResponseEntity.ok(user.getCart());
    }

    @DeleteMapping("/cart/{id}")
    public ResponseEntity<Cart> removeFromCart (@PathVariable("id") Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.getCart().removeFromCart(optionalProduct.get());

        user = userService.saveUser(user);

        return ResponseEntity.ok(user.getCart());
    }
}
