package com.ewallet.api.dto.response;

import com.ewallet.api.model.Wallet;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class WalletResponse {
    private String walletNumber;
    private BigDecimal balance;
    private String ownerName;

    public WalletResponse(String walletNumber, BigDecimal balance, String ownerName) {
        this.walletNumber = walletNumber;
        this.balance = balance;
        this.ownerName = ownerName;
    }

    public WalletResponse() {}

    public WalletResponse(Wallet wallet) {
        this.walletNumber = wallet.getWalletNumber();
        this.balance = wallet.getBalance();
        this.ownerName = wallet.getUser().getFullName();
    }

    public WalletResponse(Wallet wallet, String ownerName) {
        this.walletNumber = wallet.getWalletNumber();
        this.balance = wallet.getBalance();
        this.ownerName = ownerName;
    }
}
