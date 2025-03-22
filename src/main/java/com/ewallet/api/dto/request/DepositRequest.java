package com.ewallet.api.dto.request;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import com.ewallet.api.model.enums.TransactionType;
import lombok.Setter;

@Setter
@Getter
@Data
public class DepositRequest {
    private BigDecimal amount;

    private TransactionType type;

    public DepositRequest(BigDecimal amount) {
        this.amount = amount;
    }
}
