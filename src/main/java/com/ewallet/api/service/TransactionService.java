package com.ewallet.api.service;

import com.ewallet.api.dto.request.TransactionRequest;
import com.ewallet.api.dto.response.TransactionResponse;
import com.ewallet.api.events.TransactionEventPublisher;
import com.ewallet.api.exception.InsufficientBalanceException;
import com.ewallet.api.exception.ResourceNotFoundException;
import com.ewallet.api.model.Transaction;
import com.ewallet.api.model.TransactionCategory;
import com.ewallet.api.model.Wallet;
import com.ewallet.api.repository.TransactionCategoryRepository;
import com.ewallet.api.repository.TransactionRepository;
import com.ewallet.api.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.ewallet.api.model.enums.TransactionType.*;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionCategoryRepository categoryRepository;
    private final TransactionEventPublisher eventPublisher;

    @Autowired
    public TransactionService(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            TransactionCategoryRepository categoryRepository,
            TransactionEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Page<TransactionResponse> getTransactionsByWalletId(Long walletId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByWalletId(walletId, pageable);
        return transactions.map(this::convertToDto);
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id));
        return convertToDto(transaction);
    }

    @Transactional
    public TransactionResponse createDeposit(TransactionRequest transactionDto) {
        transactionDto.setType(DEPOSIT);
        return createTransaction(transactionDto);
    }

    @Transactional
    public TransactionResponse createWithdrawal(TransactionRequest transactionDto) {
        transactionDto.setType(WITHDRAWAL);
        return createTransaction(transactionDto);
    }

    @Transactional
    public TransactionResponse createTransfer(TransactionRequest transactionDto) {
        transactionDto.setType(TRANSFER);

        // Validate recipient wallet exists
        if (transactionDto.getWalletId() == null) {
            throw new IllegalArgumentException("Recipient wallet ID is required for transfers");
        }

        walletRepository.findById(transactionDto.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient wallet not found"));

        return createTransaction(transactionDto);
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest transactionDto) {
        // Load wallet
        Wallet wallet = walletRepository.findById(transactionDto.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // Load category if provided
        TransactionCategory category = null;
        if (transactionDto.getCategoryId() != null) {
            category = categoryRepository.findById(transactionDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        // Process transaction based on type
        switch (transactionDto.getType()) {
            case DEPOSIT:
                return processDeposit(transactionDto, wallet, category);
            case WITHDRAWAL:
                return processWithdrawal(transactionDto, wallet, category);
            case TRANSFER:
                return processTransfer(transactionDto, wallet, category);
            default:
                throw new IllegalArgumentException("Invalid transaction type");
        }
    }

    private TransactionResponse processDeposit(TransactionRequest transactionDto, Wallet wallet, TransactionCategory category) {
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(DEPOSIT);
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setWallet(wallet);
        transaction.setCategory(category);

        // Update wallet balance
        wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
        walletRepository.save(wallet);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Convert to DTO
        TransactionResponse savedDto = convertToDto(savedTransaction);

        // Publish event
        eventPublisher.publishTransactionCreatedEvent(savedDto);

        return savedDto;
    }

    private TransactionResponse processWithdrawal(TransactionRequest transactionDto, Wallet wallet, TransactionCategory category) {
        // Check if wallet has sufficient balance
        if (wallet.getBalance().compareTo(transactionDto.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in wallet");
        }

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(WITHDRAWAL);
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setWallet(wallet);
        transaction.setCategory(category);

        // Update wallet balance
        wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
        walletRepository.save(wallet);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Convert to DTO
        TransactionResponse savedDto = convertToDto(savedTransaction);

        // Publish event
        eventPublisher.publishTransactionCreatedEvent(savedDto);

        return savedDto;
    }

    private TransactionResponse processTransfer(TransactionRequest transactionDto, Wallet sourceWallet, TransactionCategory category) {
        // Check if source wallet has sufficient balance
        if (sourceWallet.getBalance().compareTo(transactionDto.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in source wallet");
        }

        // Load recipient wallet
        Wallet recipientWallet = walletRepository.findById(transactionDto.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient wallet not found"));

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setType(TRANSFER);
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setWallet(sourceWallet);
        transaction.setRecipientWalletNumber(String.valueOf(recipientWallet));
        transaction.setCategory(category);

        // Update source wallet balance
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(transaction.getAmount()));
        walletRepository.save(sourceWallet);

        // Update recipient wallet balance
        recipientWallet.setBalance(recipientWallet.getBalance().add(transaction.getAmount()));
        walletRepository.save(recipientWallet);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Convert to DTO
        TransactionResponse savedDto = convertToDto(savedTransaction);

        // Publish event
        eventPublisher.publishTransactionCreatedEvent(savedDto);

        return savedDto;
    }

    private TransactionResponse convertToDto(Transaction transaction) {
        TransactionResponse dto = new TransactionResponse();
        dto.setId(String.valueOf(transaction.getId()));
        dto.setAmount(transaction.getAmount());
        dto.setType(String.valueOf(transaction.getType()));
        dto.setDescription(transaction.getDescription());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setRecipientWalletNumber(String.valueOf(transaction.getWallet().getId()));

        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getId());
            dto.setCategoryName(transaction.getCategory().getName());
        }

        if (transaction.getRecipientWalletNumber() != null) {
            dto.setRecipientWalletNumber(transaction.getRecipientWalletNumber());
        }

        return dto;
    }
}