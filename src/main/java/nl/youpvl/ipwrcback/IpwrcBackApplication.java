package nl.youpvl.ipwrcback;

import com.github.javafaker.Faker;
import nl.youpvl.ipwrcback.model.Product;
import nl.youpvl.ipwrcback.model.Role;
import nl.youpvl.ipwrcback.model.User;
import nl.youpvl.ipwrcback.repository.ProductRepository;
import nl.youpvl.ipwrcback.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

@SpringBootApplication
public class IpwrcBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(IpwrcBackApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:4200");
            }
        };
    }

    @Bean
    CommandLineRunner run (UserService userService, ProductRepository productRepository) {
        return args -> {
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_MODERATOR"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));
            userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

            userService.saveUser(new User(null, "Youp van Leeuwen", "youpvleeuwen@gmail.com", "youpvl", "12345678", new ArrayList<>()));
            userService.saveUser(new User(null, "Lucas van Leeuwe", "lucas@gmail.com", "lucasvl", "12345678", new ArrayList<>()));
            userService.saveUser(new User(null, "Benjamin Vermeulen","jonk(O)@gmail.com", "jonkokoning", "12345678", new ArrayList<>()));
            userService.saveUser(new User(null, "Jimmy Jackie Bouwer", "sniff @gmail.com", "Sniff", "12345678", new ArrayList<>()));

            userService.grantPermissions("youpvl", "ROLE_SUPER_ADMIN");
            userService.grantPermissions("youpvl", "ROLE_ADMIN");
            userService.grantPermissions("youpvl", "ROLE_MODERATOR");
            userService.grantPermissions("youpvl", "ROLE_USER");
            userService.grantPermissions("lucasvl", "ROLE_ADMIN");
            userService.grantPermissions("jonkokoning", "ROLE_MODERATOR");

            Faker faker = new Faker();
            Integer max = faker.random().nextInt(50, 150);

            for (int x = 0; x < max; x++) {
                productRepository.save(new Product(null,
                        faker.commerce().productName(),
                        Double.valueOf(faker.commerce().price().replace(',', '.')),
                        faker.lorem().paragraphs(1).get(0),
                        faker.lorem().paragraph(faker.number().numberBetween(1, 5)),
                        faker.random().nextBoolean()
                        ));
            }
        };
    }
}
