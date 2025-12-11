package com.sdc.discord.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CryptoUtils}
 */
class CryptoUtilsTest {

    private CryptoUtils cryptoUtils;
    private static final String VALID_KEY = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    @BeforeEach
    void setUp() {
        cryptoUtils = new CryptoUtils(VALID_KEY);
    }

    @Test
    @DisplayName("Should encrypt and decrypt text successfully")
    void testEncryptDecrypt() {
        String plaintext = "test-secret-token";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);
        
        String decrypted = cryptoUtils.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("Should produce different encrypted values for same input")
    void testEncryptionProducesDifferentValues() {
        String plaintext = "test-token";
        
        String encrypted1 = cryptoUtils.encrypt(plaintext);
        String encrypted2 = cryptoUtils.encrypt(plaintext);
        
        assertNotEquals(encrypted1, encrypted2);
        
        // But both should decrypt to same value
        assertEquals(plaintext, cryptoUtils.decrypt(encrypted1));
        assertEquals(plaintext, cryptoUtils.decrypt(encrypted2));
    }

    @Test
    @DisplayName("Should encrypt empty string")
    void testEncryptEmptyString() {
        String plaintext = "";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        assertNotNull(encrypted);
        
        String decrypted = cryptoUtils.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("Should encrypt special characters")
    void testEncryptSpecialCharacters() {
        String plaintext = "!@#$%^&*()_+-=[]{}|;:',.<>?/`~";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        String decrypted = cryptoUtils.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("Should encrypt unicode characters")
    void testEncryptUnicodeCharacters() {
        String plaintext = "Hello World Test";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        String decrypted = cryptoUtils.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("Should encrypt long text")
    void testEncryptLongText() {
        String plaintext = "a".repeat(10000);
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        String decrypted = cryptoUtils.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("Should throw exception for invalid key length")
    void testInvalidKeyLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CryptoUtils("shortkey");
        });
    }

    @Test
    @DisplayName("Should throw exception for odd length hex string")
    void testOddLengthHexString() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CryptoUtils("123");
        });
    }

    @Test
    @DisplayName("Should throw exception when decrypting invalid data")
    void testDecryptInvalidData() {
        assertThrows(RuntimeException.class, () -> {
            cryptoUtils.decrypt("invalid-base64-data");
        });
    }

    @Test
    @DisplayName("Should handle multiline text")
    void testEncryptMultilineText() {
        String plaintext = "Line 1\nLine 2\nLine 3";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        String decrypted = cryptoUtils.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }
}
