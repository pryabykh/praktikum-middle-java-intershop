package com.pryabykh.intershop.dto;

public class BalanceDto {
    public BalanceDto(Long balance, boolean available) {
        this.balance = balance;
        this.available = available;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    private Long balance;

    private boolean available;
}
