package com.sdc.telegram.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {
    
    private CryptoUtils cryptoUtils;
    private static final String VALID_32_BYTE_HEX_KEY = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
    
    @BeforeEach
    void setUp() {
        cryptoUtils = new CryptoUtils(VALID_32_BYTE_HEX_KEY);
    }
    
    @Test
    void constructor_WithValid32ByteKey_ShouldSucceed() {
        assertNotNull(new CryptoUtils(VALID_32_BYTE_HEX_KEY));
    }
    
    @Test
    void constructor_WithInvalidKeyLength_ShouldThrowException() {
        String invalidKey = "0123456789abcdef"; // Only 16 characters (8 bytes)
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new CryptoUtils(invalidKey)
        );
        
        assertTrue(exception.getMessage().contains("Master key must be 32 bytes"));
    }
    
    @Test
    void constructor_WithOddLengthHexString_ShouldThrowException() {
        String oddLengthHex = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcde"; // 63 chars
        
        assertThrows(IllegalArgumentException.class, () -> new CryptoUtils(oddLengthHex));
    }
    
    @Test
    void encrypt_WithValidPlaintext_ShouldReturnBase64String() throws Exception {
        String plaintext = "Hello, World!";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
        assertNotEquals(plaintext, encrypted);
        // Base64 encoded string should only contain valid Base64 characters
        assertTrue(encrypted.matches("^[A-Za-z0-9+/=]+$"));
    }
    
    @Test
    void encrypt_WithEmptyString_ShouldSucceed() throws Exception {
        String plaintext = "";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        
        assertNotNull(encrypted);
        assertFalse(encrypted.isEmpty());
    }
    
    @Test
    void encrypt_SameTextTwice_ShouldProduceDifferentCiphertext() throws Exception {
        String plaintext = "Test Message";
        
        String encrypted1 = cryptoUtils.encrypt(plaintext);
        String encrypted2 = cryptoUtils.encrypt(plaintext);
        
        assertNotEquals(encrypted1, encrypted2, "Same plaintext should produce different ciphertext due to random IV");
    }
    
    @Test
    void decrypt_WithValidCiphertext_ShouldReturnOriginalPlaintext() throws Exception {
        String plaintext = "Secret Message";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        String decrypted = cryptoUtils.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }
    
    @Test
    void decrypt_WithEmptyStringEncrypted_ShouldReturnEmptyString() throws Exception {
        String plaintext = "";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        String decrypted = cryptoUtils.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }
    
    @Test
    void encryptAndDecrypt_WithSpecialCharacters_ShouldPreserveContent() throws Exception {
        String plaintext = "Special chars: !@#$%^&*()_+-=[]{}|;:',.<>?/~`";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        String decrypted = cryptoUtils.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }
    
    @Test
    void encryptAndDecrypt_WithUnicodeCharacters_ShouldPreserveContent() throws Exception {
        String plaintext = "Unicode: 你好世界 🌍 Привет мир";
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        String decrypted = cryptoUtils.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }
    
    @Test
    void encryptAndDecrypt_WithLongText_ShouldWork() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("Long text ");
        }
        String plaintext = sb.toString();
        
        String encrypted = cryptoUtils.encrypt(plaintext);
        String decrypted = cryptoUtils.decrypt(encrypted);
        
        assertEquals(plaintext, decrypted);
    }
    
    @Test
    void decrypt_WithInvalidBase64_ShouldThrowException() {
        String invalidBase64 = "This is not valid base64!!!";
        
        assertThrows(Exception.class, () -> cryptoUtils.decrypt(invalidBase64));
    }
    
    @Test
    void decrypt_WithTamperedCiphertext_ShouldThrowException() throws Exception {
        String plaintext = "Original Message";
        String encrypted = cryptoUtils.encrypt(plaintext);
        
        // Tamper with the ciphertext by modifying a character
        String tampered = encrypted.substring(0, encrypted.length() - 5) + "XXXX" + encrypted.charAt(encrypted.length() - 1);
        
        assertThrows(Exception.class, () -> cryptoUtils.decrypt(tampered));
    }
    
    @Test
    void decrypt_WithTooShortCiphertext_ShouldThrowException() {
        // Create a too-short Base64 encoded string (less than 12 bytes for IV)
        String tooShort = "dGVzdA=="; // "test" in Base64, only 4 bytes
        
        assertThrows(Exception.class, () -> cryptoUtils.decrypt(tooShort));
    }
}
