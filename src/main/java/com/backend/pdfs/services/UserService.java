package com.backend.pdfs.services;

import com.backend.pdfs.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService{
    @Autowired
    UserRepository userRepository;

    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public void addUser(User user){
        userRepository.insert(user);
    }

    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }
}
