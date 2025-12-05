package com.sdc.whatsapp.repository;

import com.sdc.whatsapp.domain.model.WhatsappChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WhatsappChatRepository extends JpaRepository<WhatsappChat, Long> {
    Optional<WhatsappChat> findByWhatsappAccountIdAndContactWaId(final Long accountId, final String contactWaId);
    List<WhatsappChat> findByWhatsappAccountIdOrderByLastMessageDateDesc(final Long accountId);
}
