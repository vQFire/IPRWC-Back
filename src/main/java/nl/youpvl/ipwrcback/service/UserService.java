package nl.youpvl.ipwrcback.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.youpvl.ipwrcback.model.Role;
import nl.youpvl.ipwrcback.model.User;
import nl.youpvl.ipwrcback.repository.RoleRepository;
import nl.youpvl.ipwrcback.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor @Transactional @Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = this.userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            log.error("User {} not found in the database", username);
            throw new UsernameNotFoundException("User not found in the database");
        }

        return optionalUser.get();
    }



    public Optional<User> getUser(String username) {
        log.info("Getting user {}", username);

        return this.userRepository.findByUsername(username);
    }

    public List<User> getUsers() {
        log.info("Getting all users");

        return this.userRepository.findAll();
    }

    public User saveUser(User user) {
        log.info("Saving {} to the database", user.getName());

        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return this.userRepository.save(user);
    }

    public Optional<Role> getRole(String roleName) {
        log.info("Getting the {} role from the database", roleName);

        return this.roleRepository.findByName(roleName);
    }

    public List<Role> getRoles() {
        log.info("Getting all the roles from the database");

        return this.roleRepository.findAll();
    }

    public Role saveRole(Role role) {
        log.info("Saving the {} role to the database", role.getName());

        return this.roleRepository.save(role);
    }

    public void grantPermissions(String username, String roleName) {
        log.info("Giving user {} the {} role", username, roleName);

        Optional<User> optionalUser = this.userRepository.findByUsername(username);
        Optional<Role> optionalRole = this.roleRepository.findByName(roleName);

        if (optionalUser.isEmpty() || optionalRole.isEmpty()) {
            log.warn("Either user {} or role {} is not in the database", username, roleName);
            return;
        }

        User user = optionalUser.get();
        Role role = optionalRole.get();

        user.getRoles().add(role);
    }
}
