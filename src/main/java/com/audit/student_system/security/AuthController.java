package com.audit.student_system.security;

import com.audit.student_system.dto.RegisterRequest;
import com.audit.student_system.entity.Role;
import com.audit.student_system.entity.User;
import com.audit.student_system.repository.UserRepository;
import com.audit.student_system.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(
            @RequestBody Map<String, String> request
    ) {

        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login successful",
                        Map.of(
                                "token", token,
                                "role", user.getRole().name()
                        )
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @RequestBody RegisterRequest request
    ) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User registered successfully", null)
        );
    }
}