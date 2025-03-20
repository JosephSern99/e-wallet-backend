package com.ewallet.api.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class TransferRequest {
    private String recipientWalletNumber;
    private BigDecimal amount;
}
