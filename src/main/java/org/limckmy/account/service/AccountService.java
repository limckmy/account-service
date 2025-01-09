package org.limckmy.account.service;

import org.limckmy.account.dto.AccountDTO;
import org.limckmy.account.dto.CustomerDTO;
import org.limckmy.account.entity.Account;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountService {

    Page<CustomerDTO> findAccounts(Long customerId, String accountNumber, String description, int page, int size);

    AccountDTO updateAccountDescription(Long accountId, String description);

}
