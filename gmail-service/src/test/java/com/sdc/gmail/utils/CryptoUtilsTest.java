package com.sdc.gmail.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {

    private CryptoUtils cryptoUtils;
    private static final String MASTER_KEY = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";

    @BeforeEach
    void setUp() {
        cryptoUtils = new CryptoUtils(MASTER_KEY);
    }

    @Test
    void testConstructorWithValidKey() {
        assertNotNull(cryptoUtils);
    }

    @Test
    void testConstructorWithInvalidKeyLength() {
        assertThrows(IllegalArgumentException.class, () -> new CryptoUtils("shortkey"));
    }

    @Test
    void testConstructorWithInvalidHexString() {
        assertThrows(IllegalArgumentException.class, () -> new CryptoUtils("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ"));
    }

    @Test
    void testEncryptAndDecrypt() throws Exception {
        final String plaintext = "test@gmail.com";
        
        final String encrypted = cryptoUtils.encrypt(plaintext);
        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);
        
        final String decrypted = cryptoUtils.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptProducesDifferentCiphertext() throws Exception {
        final String plaintext = "same text";
        
        final String encrypted1 = cryptoUtils.encrypt(plaintext);
        final String encrypted2 = cryptoUtils.encrypt(plaintext);
        
        assertNotEquals(encrypted1, encrypted2);
        assertEquals(plaintext, cryptoUtils.decrypt(encrypted1));
        assertEquals(plaintext, cryptoUtils.decrypt(encrypted2));
    }

    @Test
    void testDecryptWithInvalidCiphertext() {
        assertThrows(Exception.class, () -> cryptoUtils.decrypt("invalid-ciphertext"));
    }

    @Test
    void testEncryptEmptyString() throws Exception {
        final String plaintext = "";
        
        final String encrypted = cryptoUtils.encrypt(plaintext);
        assertNotNull(encrypted);
        
        final String decrypted = cryptoUtils.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptLongString() throws Exception {
        final String plaintext = "This is a very long string that needs to be encrypted and decrypted properly to ensure the encryption algorithm works correctly with larger data sizes";
        
        final String encrypted = cryptoUtils.encrypt(plaintext);
        assertNotNull(encrypted);
        
        final String decrypted = cryptoUtils.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void testEncryptSpecialCharacters() throws Exception {
        final String plaintext = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        
        final String encrypted = cryptoUtils.encrypt(plaintext);
        assertNotNull(encrypted);
        
        final String decrypted = cryptoUtils.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }
}
