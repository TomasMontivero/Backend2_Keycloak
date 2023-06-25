package com.dh.msusers.repository;

import com.dh.msusers.model.User;

public interface IUserRepository {

    public User findById(String id);

}
