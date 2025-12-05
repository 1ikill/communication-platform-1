package com.sdc.whatsapp.repository;

import com.sdc.whatsapp.domain.model.WhatsappMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WhatsappMessageRepository extends JpaRepository<WhatsappMessage, Long> {
    List<WhatsappMessage> findByWhatsappAccountIdOrderByCreatedDateDesc(final Long accountId);
    List<WhatsappMessage> findByWhatsappChatIdOrderByCreatedDateAsc(final Long chatId);
}
