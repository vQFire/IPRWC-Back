package nl.youpvl.ipwrcback.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.youpvl.ipwrcback.model.User;
import nl.youpvl.ipwrcback.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        Optional<User> optionalUser = userRepository.findByUsername(username);

        log.info("Attempting authentication for {}", username);

        if (optionalUser.isPresent()) {
            log.info("User has been found");

            User user = optionalUser.get();

            log.info(user.toString());

            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("Passwords matched");

                return new UsernamePasswordAuthenticationToken(user, authentication.getCredentials(), user.getAuthorities());
            }

            log.warn("Passwords did not match");
        }

        throw new BadCredentialsException(String.format("User %s could not log in.", username));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
