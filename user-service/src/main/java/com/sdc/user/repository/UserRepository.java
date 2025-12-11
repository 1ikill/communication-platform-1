package com.sdc.user.repository;

import com.sdc.user.domain.exception.NotFoundException;
import com.sdc.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link User}
 * @since 10.2025
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(final String username);

    Optional<User> findByEmail(final String email);

    boolean existsByEmail(final String email);

    boolean existsByUsername(final String username);

    @Override
    default User getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Can't find User with id=%s".formatted(id)));
    }

}
