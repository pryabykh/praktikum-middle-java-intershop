package com.pryabykh.intershop.service;

import com.pryabykh.intershop.entity.User;
import com.pryabykh.intershop.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Long fetchDefaultUserId() {
        return userRepository.findAll(Sort.by("id")).stream().findFirst().map(User::getId).orElseThrow();
    }
}
