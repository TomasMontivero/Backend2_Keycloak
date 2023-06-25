package com.dh.msusers.service;

import com.dh.msusers.model.User;
import com.dh.msusers.repository.KeyCloakUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeyCloakUserRepository keyCloakUserRepository;

    /*public UserService(KeyCloakUserRepository keyCloakUserRepository) {
        this.keyCloakUserRepository = keyCloakUserRepository;
    }*/

    public User getUserById(String id) {
        return keyCloakUserRepository.findById(id);
    }

}
