package com.ewallet.api.dto.request;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Data
public class DepositRequest {
    private BigDecimal amount;

    public DepositRequest(BigDecimal amount) {
        this.amount = amount;
    }
}
