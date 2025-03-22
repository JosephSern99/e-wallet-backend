package com.ewallet.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "transaction_categories")
public class TransactionCategory extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "icon_name")
    private String iconName;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new HashSet<>();
}
