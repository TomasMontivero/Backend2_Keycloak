package com.dh.msusers.service;

import com.dh.msusers.model.Bill;
import com.dh.msusers.model.User;
import com.dh.msusers.model.UserAndBills;
import com.dh.msusers.repository.KeyCloakUserRepository;
import com.dh.msusers.repository.feign.BillsFeignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeyCloakUserRepository keyCloakUserRepository;
    private final BillsFeignRepository billsFeignRepository;

    /*public UserService(KeyCloakUserRepository keyCloakUserRepository) {
        this.keyCloakUserRepository = keyCloakUserRepository;
    }*/

    public User getUserById(String id) {
        return keyCloakUserRepository.findById(id);
    }

    public UserAndBills getUserAndBillsById(String id) {
        User user = getUserById(id);
        //List<Bill> bills = billsFeignRepository.findByCustomerId(id);
        List<Bill> bills = billsFeignRepository.findByCustomerId(user.getUsername());
        return new UserAndBills(user.getId(), user.getUsername(), user.getEmail(), user.getFirstname(), bills);
    }

    public ResponseEntity getHelloBills() {
        return billsFeignRepository.getHelloBills();
    }

}
