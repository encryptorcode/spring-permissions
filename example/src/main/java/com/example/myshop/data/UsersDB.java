package com.example.myshop.data;

import com.example.myshop.entities.User;

public class UsersDB {
    public static DB<User> get() {
        return userDB;
    }

    private static final DB<User> userDB = new DB<>();

    static {
        userDB.insert(new User(1L, "abhay", "abhay@mail.com", "1.png", User.Role.USER));
        userDB.insert(new User(2L, "admin", "admin@myshop.com", "2.png", User.Role.ADMIN));
        userDB.insert(new User(3L, "beast", "beast@gmail.com", "3.png", User.Role.USER));
        userDB.insert(new User(4L, "carrot", "carrot@live.com", "4.png", User.Role.USER));
        userDB.insert(new User(5L, "dean", "dean@myshop.com", "5.png", User.Role.ADMIN));
    }
}
