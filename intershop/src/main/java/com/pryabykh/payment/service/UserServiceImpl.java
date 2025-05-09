package com.pryabykh.payment.service;

import com.pryabykh.payment.entity.User;
import com.pryabykh.payment.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> fetchDefaultUserId() {
        return userRepository.findAll(Sort.by("id"))
                .next()
                .map(User::getId)
                .switchIfEmpty(Mono.error(new RuntimeException()));
    }
}
