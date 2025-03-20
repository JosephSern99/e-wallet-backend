package com.ewallet.api.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Setter
@Getter
public class WithdrawRequest {
    private BigDecimal amount;
}
