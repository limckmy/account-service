package org.limckmy.account.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountBatchDTO {
    private Long accountId;
    private String accountNumber;
    private String accountType;
    private String accountDescription;
    private BigDecimal balance;
    private Long customerId;
}
