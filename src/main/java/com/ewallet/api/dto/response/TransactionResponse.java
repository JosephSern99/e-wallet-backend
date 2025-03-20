package com.ewallet.api.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class TransactionResponse {
    private String transactionReference;
    private String type;
    private BigDecimal amount;
    private String description;
    private String recipientWalletNumber;
    private String status;
    private LocalDateTime createdAt;

    public TransactionResponse(String transactionReference, String type, BigDecimal amount, String description, String recipientWalletNumber, String status, LocalDateTime createdAt) {
        this.transactionReference = transactionReference;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.recipientWalletNumber = recipientWalletNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    public TransactionResponse() {}

}
