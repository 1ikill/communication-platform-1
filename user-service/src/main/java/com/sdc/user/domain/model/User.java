package com.sdc.user.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdc.user.domain.constants.RoleType;
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

/**
 * User model.
 * @since 10.2025
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "user_service")
public class User implements Persistable<Long> {
    /**
     * ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User email.
     */
    private String email;

    /**
     * Username.
     */
    private String username;

    /**
     * Password hash.
     */
    private String passwordHash;

    /**
     * User full name
     */
    private String fullName;

    /**
     * User role.
     */
    @Enumerated(EnumType.STRING)
    private RoleType role;

    /**
     * User creation date.
     */
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdDate;

    /**
     * User last modified date.
     */
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime lastModifiedDate;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
