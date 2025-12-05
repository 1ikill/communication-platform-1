package com.sdc.whatsapp.repository;

import com.sdc.whatsapp.domain.model.WhatsappCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WhatsappCredentialsRepository extends JpaRepository<WhatsappCredentials, Long> {
    Optional<WhatsappCredentials> findByPhoneNumberId(final String phoneNumberId);
    List<WhatsappCredentials> findByUserId(final Long userId);
    Optional<WhatsappCredentials> findByWebhookVerifyToken(String token);
}
