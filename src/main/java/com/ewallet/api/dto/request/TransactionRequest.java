package com.ewallet.api.dto.request;

import com.ewallet.api.model.enums.TransactionType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class TransactionRequest {
    private Long walletId;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private Long categoryId;
    private String recipientWalletNumber;
}
