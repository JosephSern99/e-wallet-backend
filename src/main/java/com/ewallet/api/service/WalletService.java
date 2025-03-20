package com.ewallet.api.service;

import com.ewallet.api.exception.InsufficientBalanceException;
import com.ewallet.api.exception.ResourceNotFoundException;
import com.ewallet.api.model.Transaction;
import com.ewallet.api.model.User;
import com.ewallet.api.model.Wallet;
import com.ewallet.api.model.enums.TransactionStatus;
import com.ewallet.api.model.enums.TransactionType;
import com.ewallet.api.repository.TransactionRepository;
import com.ewallet.api.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    UlidService ulidService;

    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setWalletNumber(generateWalletNumber());
        wallet.setBalance(BigDecimal.ZERO);
        return walletRepository.save(wallet);
    }

    public Wallet getWalletByUser(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for this user"));
    }

    @Transactional
    public Transaction deposit(Wallet wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setWallet(wallet);
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDescription("Deposit to wallet");

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdraw(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setWallet(wallet);
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDescription("Withdrawal from wallet");

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction transfer(Wallet senderWallet, String recipientWalletNumber, BigDecimal amount) {
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }

        Wallet recipientWallet = walletRepository.findByWalletNumber(recipientWalletNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient wallet not found"));

        // Deduct from sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        walletRepository.save(senderWallet);

        // Add to recipient
        recipientWallet.setBalance(recipientWallet.getBalance().add(amount));
        walletRepository.save(recipientWallet);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(amount);
        transaction.setWallet(senderWallet);
        transaction.setRecipientWalletNumber(recipientWalletNumber);
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDescription("Transfer to " + recipientWalletNumber);

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionHistory(Wallet wallet) {
        return transactionRepository.findByWalletOrderByCreatedAtDesc(wallet);
    }

    private String generateWalletNumber() {
        return "W" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
    }

    public String generateTransactionReference() {
        return "TXN" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 15).toUpperCase();
    }
}
