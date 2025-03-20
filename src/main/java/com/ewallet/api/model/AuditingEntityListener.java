package com.ewallet.api.model;
import com.ewallet.api.security.AuthenticationContext;
import com.ewallet.api.security.SecurityUser;
import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Component
public class AuditingEntityListener {
    @Autowired
    private AuthenticationContext authContext;
    private static final SecureRandom random = new SecureRandom();

    @PrePersist
    public void onPrePersist(BaseEntity entity) {
        if (entity.getId() == null) {
            entity.setId(random.nextLong());
        }

        entity.createdBy = getCurrentLoggedInUser() != null ? getCurrentLoggedInUser().getUsername() : "system";
        entity.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onPreUpdate(BaseEntity entity) {
        entity.updatedBy = getCurrentLoggedInUser() != null ? getCurrentLoggedInUser().getUsername() : "system";
        entity.updatedDate = LocalDateTime.now();
    }

    private SecurityUser getCurrentLoggedInUser() {
        return authContext.getCurrentLoggedInUser();
    }
}