package com.lookator.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    AuthResponse register(@RequestBody AuthRequest request) {
        validate(request);
        var email = request.email().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already registered");
        }
        var user = users.save(new AppUser(email, passwordEncoder.encode(request.password())));
        return new AuthResponse(jwtService.createToken(user), user.getId(), user.getEmail());
    }

    @PostMapping("/login")
    AuthResponse login(@RequestBody AuthRequest request) {
        validate(request);
        var user = users.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return new AuthResponse(jwtService.createToken(user), user.getId(), user.getEmail());
    }

    private void validate(AuthRequest request) {
        if (request.email() == null || !request.email().contains("@")) {
            throw new IllegalArgumentException("A valid email is required");
        }
        if (request.password() == null || request.password().length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }
    }
}
