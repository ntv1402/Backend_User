package com.training.backend.controller;

import com.training.backend.config.jwt.AuthUserDetails;
import com.training.backend.config.jwt.JwtUtils;
import com.training.backend.config.jwt.UserDetailsServiceImpl;
import com.training.backend.payload.request.LoginRequest;
import com.training.backend.payload.response.LoginResponse;
import com.training.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    final JwtUtils jwtUtils;
    final AuthenticationManager authenticationManager;
    final UserDetailsServiceImpl userDetailsService;

    AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();
            String accessToken = jwtUtils.generateToken(userDetails);
            return new LoginResponse(accessToken);
        } catch (UsernameNotFoundException | BadCredentialsException ex) {
            logger.warn(ex.getMessage());
            errors.put("code" , "100");
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
            errors.put("code" , "000");
        }
        return new LoginResponse(errors);
    }


//    @GetMapping("/test-auth")
//    public ResponseEntity<String> testToken(Authentication authentication) {
//        if (authentication != null && authentication.isAuthenticated()) {
//            return ResponseEntity.ok("Token OK - User: " + authentication.getName());
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token missing or invalid");
//    }

}
