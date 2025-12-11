package com.sdc.gmail.config;

import com.sdc.gmail.utils.CryptoUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CryptoConfig.class)
@TestPropertySource(properties = {
        "credentials.secret-key=0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF"
})
class CryptoConfigTest {

    @Autowired(required = false)
    private CryptoUtils cryptoUtils;

    @Test
    void testCryptoUtilsBean() {
        assertNotNull(cryptoUtils);
    }
}
