package com.ewallet.api.controller;

import com.ewallet.api.dto.request.DepositRequest;
import com.ewallet.api.dto.request.TransferRequest;
import com.ewallet.api.dto.request.WithdrawRequest;
import com.ewallet.api.dto.response.ApiResponse;
import com.ewallet.api.dto.response.TransactionResponse;
import com.ewallet.api.dto.response.WalletResponse;
import com.ewallet.api.model.Transaction;
import com.ewallet.api.model.User;
import com.ewallet.api.model.Wallet;
import com.ewallet.api.service.UserService;
import com.ewallet.api.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/")
@Slf4j
public class WalletController {
    @Autowired
    private final WalletService walletService;

    @Autowired
    private final UserService userService;

    public WalletController(WalletService walletService, UserService userService) {
        this.walletService = walletService;
        this.userService = userService;
    }

    @PostMapping("wallet/create")
    public ResponseEntity<WalletResponse> createWallet(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        walletService.createWallet(user);

        WalletResponse response = new WalletResponse();
        response.setWalletNumber("W123456");
        response.setBalance(BigDecimal.valueOf(100));
        response.setOwnerName(user.getFullName());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/wallet")
    public ResponseEntity<WalletResponse> getWallet(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Wallet wallet = walletService.getWalletByUser(user);

        WalletResponse response = new WalletResponse();
        response.setWalletNumber(wallet.getWalletNumber());
        response.setBalance(wallet.getBalance());
        response.setOwnerName(user.getFullName());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/wallet/deposit")
    public ResponseEntity<ApiResponse> deposit(
            @Valid @RequestBody DepositRequest request,
            Authentication authentication) {

        User user = userService.getUserByUsername(authentication.getName());
        Wallet wallet = walletService.getWalletByUser(user);
        log.debug("Wallet: {}", wallet);
        log.debug("Amount: {}", request.getAmount());
        Transaction transaction = walletService.deposit(wallet, request.getAmount());

        return ResponseEntity.ok(new ApiResponse(
                true,
                "Deposit successful",
                transaction.getTransactionReference()
        ));
    }

    @PostMapping("/wallet/withdraw")
    public ResponseEntity<ApiResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication) {

        User user = userService.getUserByUsername(authentication.getName());
        Wallet wallet = walletService.getWalletByUser(user);

        Transaction transaction = walletService.withdraw(wallet, request.getAmount());

        return ResponseEntity.ok(new ApiResponse(
                true,
                "Withdrawal successful",
                transaction.getTransactionReference()
        ));
    }

    @PostMapping("/wallet/transfer")
    public ResponseEntity<ApiResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        User user = userService.getUserByUsername(authentication.getName());
        Wallet wallet = walletService.getWalletByUser(user);

        Transaction transaction = walletService.transfer(wallet, request.getRecipientWalletNumber(), request.getAmount());

        return ResponseEntity.ok(new ApiResponse(
                true,
                "Transfer successful",
                transaction.getTransactionReference()
        ));
    }

    @GetMapping("/wallet/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Wallet wallet = walletService.getWalletByUser(user);

        List<Transaction> transactions = walletService.getTransactionHistory(wallet);

        List<TransactionResponse> response = transactions.stream()
                .map(transaction -> {
                    TransactionResponse dto = new TransactionResponse();
                    dto.setTransactionReference(transaction.getTransactionReference());
                    dto.setType(transaction.getType().name());
                    dto.setAmount(transaction.getAmount());
                    dto.setDescription(transaction.getDescription());
                    dto.setRecipientWalletNumber(transaction.getRecipientWalletNumber());
                    dto.setStatus(transaction.getStatus().name());
                    dto.setCreatedAt(transaction.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
