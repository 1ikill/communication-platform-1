package com.sdc.discord.utils;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for encrypting/decrypting sensitive data using AES-256-GCM.
 * @since 11.2025
 */
@Component
public class CryptoUtils {
    private static final String ALGO = "AES/GCM/NoPadding";
    private static final String KEY_ALGO = "AES";
    private static final int TAG_LENGTH = 128; // bits
    private static final int IV_LENGTH = 12;   // bytes

    private final byte[] masterKey;

    /**
     * Constructor receives master key from configuration.
     *
     * @param masterKeyHex 32-byte hex string (from application.yml/env var)
     */
    public CryptoUtils(final String masterKeyHex) {
        this.masterKey = hexStringToByteArray(masterKeyHex);
        if (masterKey.length != 32) {
            throw new IllegalArgumentException("Master key must be 32 bytes (256 bits)");
        }
    }

    /**
     * Encrypts a plaintext string and returns Base64-encoded ciphertext.
     *
     * @param plaintext the text to encrypt
     * @return Base64 encoded ciphertext
     */
    public String encrypt(final String plaintext) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGO);
        }  catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        final byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        final GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        final SecretKey key = new SecretKeySpec(masterKey, KEY_ALGO);

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        final byte[] encrypted;
        try {
            encrypted = cipher.doFinal(plaintext.getBytes());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        final byte[] encryptedWithIv = new byte[IV_LENGTH + encrypted.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, IV_LENGTH);
        System.arraycopy(encrypted, 0, encryptedWithIv, IV_LENGTH, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }

    /**
     * Decrypts a Base64-encoded ciphertext string.
     *
     * @param ciphertext Base64-encoded encrypted text
     * @return Decrypted plaintext
     */
    public String decrypt(final String ciphertext) {
        final byte[] decoded = Base64.getDecoder().decode(ciphertext);

        final byte[] iv = new byte[IV_LENGTH];
        final byte[] encrypted = new byte[decoded.length - IV_LENGTH];

        System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
        System.arraycopy(decoded, IV_LENGTH, encrypted, 0, encrypted.length);

        final Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGO);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        final GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        final SecretKey key = new SecretKeySpec(masterKey, KEY_ALGO);
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        try {
            return new String(cipher.doFinal(encrypted));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a hex string to a byte array.
     *
     * @param source hex string
     * @return byte array
     */
    private static byte[] hexStringToByteArray(final String source) {
        final int len = source.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string length");
        }
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(source.charAt(i), 16) << 4)
                    + Character.digit(source.charAt(i + 1), 16));
        }
        return data;
    }
}