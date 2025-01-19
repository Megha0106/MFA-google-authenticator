package com.example.mfa_google_authenticator.controllers;

import com.example.mfa_google_authenticator.entity.*;
import com.example.mfa_google_authenticator.repositories.UserRepository;
import com.example.mfa_google_authenticator.services.GAService;
import com.example.mfa_google_authenticator.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private GAService gaService;

    @Autowired
    UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public ResponseEntity<Map> index() {

        return ResponseEntity.ok(Map.of("users",userRepository.findAllById(List.of(1L))));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try{
            User user = userService.registerUser(registerRequest.getUserName(),registerRequest.getPassword());
            user.setMfaSecret(null);
            user.setPassword(null);
            return ResponseEntity.ok(user);
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().body("Something went wrong");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUserName(), loginRequest.getPassword(),loginRequest.getId());
        if (user != null) {
            return ResponseEntity.ok(MfaVerificationResponse.builder()
                    .userName(loginRequest.getUserName())
                    .tokenValid(Boolean.FALSE)
                    .authValid(Boolean.TRUE)
                    .mfaRequired(Boolean.TRUE)
                    .message("User authenticated using username and password")
                    .build());
        }

        return ResponseEntity.ok(MfaVerificationResponse.builder()
                .userName(loginRequest.getUserName())
                .tokenValid(Boolean.FALSE)
                .authValid(Boolean.FALSE)
                .mfaRequired(Boolean.FALSE)
                .message("Invalid credentials. Please try again.")
                .build());
    }

    @GetMapping("qr/generate")
    public void generateQR(@RequestParam String userName, HttpServletResponse response) {
        BufferedImage qrImage = userService.generateTotpQR(userName);
        if (qrImage != null) {
            try {
                response.setContentType(MediaType.IMAGE_PNG_VALUE);
                OutputStream outputStream = response.getOutputStream();
                ImageIO.write(qrImage, "png", outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/verifyTotp")
    public ResponseEntity<?> verifyTwoFactor(@RequestBody VerifyTotpRequest verifyTotpRequest) {
        MfaVerificationResponse mfaVerificationResponse = MfaVerificationResponse.builder()
                .userName(verifyTotpRequest.getUserName())
                .tokenValid(Boolean.FALSE)
                .message("Token is not valid. Please try again.")
                .build();

        if (userService.verifyTotp(verifyTotpRequest.getUserName(), verifyTotpRequest.getCode())) {
            mfaVerificationResponse = MfaVerificationResponse.builder()
                    .userName(verifyTotpRequest.getUserName())
                    .tokenValid(Boolean.TRUE)
                    .jwt("DUMMYJWT")
                    .message("Token is valid.")
                    .build();
        }

        return ResponseEntity.ok(mfaVerificationResponse);
    }


}
