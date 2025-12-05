package com.sdc.whatsapp.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdc.whatsapp.domain.constants.MessageDirectionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "whatsapp_messages", schema = "whatsapp_service")
public class WhatsappMessage implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long whatsappAccountId;
    private Long whatsappChatId;

    @Enumerated(EnumType.STRING)
    private MessageDirectionType direction;
    private String senderWaId;
    private String receiverWaId;
    private String messageId;
    private String type; //todo ?
    private String textBody;

    @Column(columnDefinition = "jsonb")
    private String rawPayload;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedDate;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
