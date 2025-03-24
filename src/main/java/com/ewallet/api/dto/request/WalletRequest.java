package com.ewallet.api.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Data
public class WalletRequest {
    private final String walletNumber;
    private final BigDecimal balance;
    private final String ownerName;
}
