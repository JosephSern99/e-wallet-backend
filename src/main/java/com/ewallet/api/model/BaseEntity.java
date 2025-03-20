package com.ewallet.api.model;

import com.github.f4b6a3.ulid.Ulid;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    public String createdBy;

    @Column(nullable = false)
    public LocalDateTime createdAt;

    public String updatedBy;

    public LocalDateTime updatedDate;
}
