package com.ewallet.api.dto.request;

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
}
