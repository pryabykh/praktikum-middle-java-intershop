package com.pryabykh.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "payment.accounts")
public class Account {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("balance")
    private Long balance;
}
