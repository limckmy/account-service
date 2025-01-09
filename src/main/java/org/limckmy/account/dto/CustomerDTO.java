package org.limckmy.account.dto;

import java.util.List;

public record CustomerDTO(Long customerId, String name, String email, String phone, List<AccountDTO> accounts) {
}
