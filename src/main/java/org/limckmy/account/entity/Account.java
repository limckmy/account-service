package org.limckmy.account.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "account")
public class Account {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)  // auto-generated for primary key
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "account_number", nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "account_type", nullable = false, length = 50)
    private String accountType;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "account_description", nullable = true, length = 255)
    private String accountDescription;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY)  // Many accounts can be associated with one customer
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;
}
