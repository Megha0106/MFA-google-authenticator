package com.example.mfa_google_authenticator.services;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class GAService {

    private static final String ISSUER = "sms";
    //generate TOTP key
    public String generateKey(){
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();
        return googleAuthenticatorKey.getKey();
    }

    //validate TOTP code
    public boolean isValid(String secret, int code){
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator(new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().build());

        return googleAuthenticator.authorize(secret, code);
    }

    //Generate QR code URL for Google Authenticator
    public BufferedImage generateQRUrl(String secret, String userName){
        String url = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(ISSUER,userName,new GoogleAuthenticatorKey.Builder(secret).build());
        try{
            return generateQRImage(url);
        }
        catch (Exception e){
            return null;
        }
    }

    //generate QR code in base64 format
    public static BufferedImage generateQRImage(String qrCodeText) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 200, 200, hintMap);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
