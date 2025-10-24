package com.example.financial;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
public class SecurityUtil {
    // !! IMPORTANT: Use a secure, randomly generated, 16-byte key and store it safely (e.g., env variable).
    // This hardcoded key is for DEMONSTRATION ONLY.
    private static final String AES_KEY = "ThisIsA16ByteKey"; // 16 bytes = AES-128
    private static final String ALGORITHM = "AES";

    // --- Password Hashing (SHA-256) ---
    // In production, use BCrypt or Argon2 instead of simple SHA-256.
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // --- Data Encryption (AES) ---
    public static byte[] encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    // --- Data Decryption (AES) ---
    public static String decrypt(byte[] encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
