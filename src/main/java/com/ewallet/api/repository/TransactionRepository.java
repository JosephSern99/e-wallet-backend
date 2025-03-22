package com.ewallet.api.repository;

import com.ewallet.api.model.Transaction;
import com.ewallet.api.model.Wallet;
import com.ewallet.api.model.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
   List<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);

    List<Transaction> findByWalletIdAndCreatedAtBetweenAndType(Long walletId, LocalDateTime start, LocalDateTime end, TransactionType transactionType);

    List<Transaction> findByWalletUserIdAndCreatedAtBetweenAndType(Long currentUserId, LocalDateTime start, LocalDateTime end, TransactionType transactionType);

    Page<Transaction> findByWalletId(Long walletId, Pageable pageable);
}
