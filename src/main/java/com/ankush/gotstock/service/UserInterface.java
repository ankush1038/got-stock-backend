package com.ankush.gotstock.service;

import com.ankush.gotstock.model.User;

import java.util.Optional;

public interface UserInterface {
    public Optional<User> getUserByUsername(String username);
    public boolean matchPassword(String rawPassword, String encodedPassword);
    public boolean existsByEmail(String email);
}
