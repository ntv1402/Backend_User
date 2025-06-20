package com.training.backend.config.jwt;

import com.training.backend.entity.User;
import com.training.backend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    final UserRepository userRepository;
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username ) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        Collection<GrantedAuthority> roles;

        if (user.isPresent()) {

            // fix all user with ROLE_USER
            roles = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
            return new AuthUserDetails(user.get(), roles);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        }
}
