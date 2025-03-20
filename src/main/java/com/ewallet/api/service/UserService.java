package com.ewallet.api.service;

import com.ewallet.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ewallet.api.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByUsername(String name) {
        return userRepository.findByUsername(name);
    }

    public Optional<User> getById(String id) {
        return userRepository.findById(Long.valueOf(id));
    }
}
