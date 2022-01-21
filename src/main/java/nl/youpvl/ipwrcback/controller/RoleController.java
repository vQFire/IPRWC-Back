package nl.youpvl.ipwrcback.controller;

import lombok.RequiredArgsConstructor;
import nl.youpvl.ipwrcback.model.Role;
import nl.youpvl.ipwrcback.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
public class RoleController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Role>> getRoles () {
        return ResponseEntity.ok().body(userService.getRoles());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Role> getRole (@PathVariable("name") String name) {
        Optional<Role> optionalRole = userService.getRole(name);

        if (optionalRole.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/role").toUriString());
        return ResponseEntity.created(uri).body(optionalRole.get());
    }

    @PostMapping
    public ResponseEntity<Role> saveRole (@RequestBody Role role) {
        return ResponseEntity.ok().body(userService.saveRole(role));
    }
}
