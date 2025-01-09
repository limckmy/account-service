package org.limckmy.account.dto;

import java.math.BigDecimal;

public record AccountDTO(Long accountId, String accountNumber, String accountType, String accountDescription, BigDecimal balance) {
}
