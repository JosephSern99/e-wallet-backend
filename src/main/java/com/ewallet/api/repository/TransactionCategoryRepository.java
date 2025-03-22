package com.ewallet.api.repository;


import org.springframework.stereotype.Repository;
import com.ewallet.api.model.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
}
