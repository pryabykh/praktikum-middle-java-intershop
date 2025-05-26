package com.pryabykh.intershop.service;

import com.pryabykh.intershop.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByName(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getName())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build()
                )
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
    }
}
