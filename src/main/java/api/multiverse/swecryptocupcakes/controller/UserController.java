package api.multiverse.swecryptocupcakes.controller;

import api.multiverse.swecryptocupcakes.payload.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final InMemoryUserDetailsManager userDetailsService;
    private final PasswordEncoder passwordEncoder;
    public UserController(ApplicationContext context) {
        this.userDetailsService = (InMemoryUserDetailsManager) context.getBean("userDetailsService");
        this.passwordEncoder = (PasswordEncoder) context.getBean("passwordEncoder");
    }
    @GetMapping
    public ResponseEntity<String> loginUser(HttpServletRequest request) {
        return new ResponseEntity<>((String) request.getAttribute("username"), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userData) {
        if (userDetailsService.userExists(userData.getUsername())) {
            return new ResponseEntity<>("Already registered", HttpStatus.CONFLICT);
        }
        UserDetails newUser = User.builder().username(userData.getUsername()).password(passwordEncoder.encode(userData.getPassword())).roles("USER")
                .build();
        userDetailsService.createUser(newUser);
        return new ResponseEntity<String>(newUser.getUsername(), HttpStatus.OK);
    }
}
