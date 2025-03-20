package com.ewallet.api.repository;

import com.ewallet.api.model.User;
import com.ewallet.api.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);

    Optional<Wallet> findByWalletNumber(String walletNumber);
}
