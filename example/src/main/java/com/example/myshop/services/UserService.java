package com.example.myshop.services;

import com.example.myshop.data.DB;
import com.example.myshop.data.UsersDB;
import com.example.myshop.entities.User;

public class UserService {

    private final DB<User> userDB = UsersDB.get();

    public User getUser(Long id) {
        return userDB.get(id);
    }
}
