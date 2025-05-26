package com.pryabykh.intershop.service;

import com.pryabykh.intershop.entity.User;
import com.pryabykh.intershop.repository.UserRepository;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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
    public Mono<Long> fetchCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext().flatMap(context -> {
            return userRepository.findByName(context.getAuthentication().getName())
                    .map(User::getId);
        }).switchIfEmpty(Mono.just(0L));
    }
}
