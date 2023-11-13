package com.web_app.web_app.controlleur;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.web_app.web_app.DTO.AuthCredentialsRequest;
import com.web_app.web_app.config.JwtUtil;
import com.web_app.web_app.models.User;

@RestController
@RequestMapping("/api/auth")  // Add a base path for the controller
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")  // Provide the full path
    public ResponseEntity<?> login(@RequestBody AuthCredentialsRequest req) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

            User user = (User) authenticate.getPrincipal();
            user.setPassword(null);
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, jwtUtil.generateToken(user))
                    .body(user);
        } catch (UsernameNotFoundException e) {  // Adjust the exception type to match your authentication provider
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
