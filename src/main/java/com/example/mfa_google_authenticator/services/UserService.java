package com.example.mfa_google_authenticator.services;

import com.example.mfa_google_authenticator.entity.User;
import com.example.mfa_google_authenticator.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GAService gaService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(String userName, String password) {
        User user = new User();
        user.setUserName(userName);
        user.setPassword(passwordEncoder.encode(password));
        user.setMfaEnabled(true);
        user.setMfaSecret(gaService.generateKey());
        return userRepository.save(user);
    }

    public User login(String userName, String password,Long id) {
        User user= userRepository.findByUserName(userName);
        if (user != null && passwordEncoder.matches(password,user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean verifyTotp(String username, int code) {
        User user = this.findByUsername(username);
        return user != null && gaService.isValid(user.getMfaSecret(), code);
    }

    public BufferedImage generateTotpQR(String username) {
        User user = this.findByUsername(username);
        return gaService.generateQRUrl(user.getMfaSecret(), username);
    }

    public User findByUsername(String userName) {
        try{
            return userRepository.findByUserName(userName);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
