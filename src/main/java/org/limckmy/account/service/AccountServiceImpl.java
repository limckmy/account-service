package org.limckmy.account.service;

import lombok.extern.slf4j.Slf4j;
import org.limckmy.account.dto.AccountDTO;
import org.limckmy.account.dto.CustomerDTO;
import org.limckmy.account.entity.Account;
import org.limckmy.account.entity.Customer;
import org.limckmy.account.repository.AccountRepository;
import org.limckmy.account.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    @Value("${account.update.attempt}")
    private int accountUpdateAttempt;


    public AccountServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }


    @Override
    public Page<CustomerDTO> findAccounts(Long customerId, String accountNumber, String description, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accountPage = accountRepository.findAccounts(customerId, accountNumber, description, pageable);

        List<CustomerDTO> customerDTOList = new ArrayList<>();

        // Group accounts by customerId and prepare the DTOs
        accountPage.getContent().stream()
                .collect(java.util.stream.Collectors.groupingBy(account -> account.getCustomer().getCustomerId()))
                .forEach((customerIdKey, accountList) -> {
                    Optional<Customer> customerOpt = customerRepository.findById(customerIdKey);
                    customerOpt.ifPresent(customer -> {
                        List<AccountDTO> accountDTOs = new ArrayList<>();
                        for (Account account : accountList) {
                            AccountDTO accountDTO = new AccountDTO(
                                    account.getAccountId(),
                                    account.getAccountNumber(),
                                    account.getAccountType(),
                                    account.getAccountDescription(),
                                    account.getBalance()
                            );
                            accountDTOs.add(accountDTO);
                        }

                        CustomerDTO customerDTO = new CustomerDTO(
                                customer.getCustomerId(),
                                customer.getName(),
                                customer.getEmail(),
                                customer.getPhone(),
                                accountDTOs
                        );
                        customerDTOList.add(customerDTO);
                    });
                });

        // Return the page with the customerDTO list
        return new PageImpl<>(customerDTOList, pageable, accountPage.getTotalElements());
    }

    @Override
    public AccountDTO updateAccountDescription(Long accountId, String description) {
        int attempt = 0;
        Account savedAccount = null;

        while (attempt < accountUpdateAttempt) {

            Optional<Account> optionalAccount = accountRepository.findById(accountId);
            if (optionalAccount.isEmpty()) {
                throw new RuntimeException("Account not found");
            }

            Account account = optionalAccount.get();
            account.setAccountDescription(description);


            try {
                savedAccount = accountRepository.save(account);
                log.info("Updated description successfully for account: {}, attempt: {}", accountId, attempt);
                break;
            } catch (Exception e) {
                log.error("Failed to update description for account: {}, attempt: {}", accountId, attempt);
                log.error(e.getClass().getName() + ": " + e.getMessage());
                attempt++;

                try {
                    Thread.sleep(500 * attempt);
                } catch (InterruptedException ie) {
                    log.error("Thread interrupted while sleeping");
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (savedAccount == null) {
            throw new RuntimeException("Failed to update description after " + accountUpdateAttempt + " retries");
        }

        return new AccountDTO(
                savedAccount.getAccountId(),
                savedAccount.getAccountNumber(),
                savedAccount.getAccountType(),
                savedAccount.getAccountDescription(),
                savedAccount.getBalance());
    }
}
