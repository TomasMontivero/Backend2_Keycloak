package com.dh.msusers.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserAndBills {

    public UserAndBills(String id, String username, String email, String firstname, List<Bill> bills) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.bills = bills;
    }

    private String id;
    private String username;
    private String email;
    private String firstname;
    private List<Bill> bills;

}
