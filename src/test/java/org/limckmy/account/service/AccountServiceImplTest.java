package org.limckmy.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.limckmy.account.dto.AccountDTO;
import org.limckmy.account.dto.CustomerDTO;
import org.limckmy.account.entity.Account;
import org.limckmy.account.entity.Customer;
import org.limckmy.account.repository.AccountRepository;
import org.limckmy.account.repository.CustomerRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Customer customer;
    private Account account;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("John Doe");
        customer.setEmail("johndoe@example.com");
        customer.setPhone("123-456-7890");
        customer.setVersion(1);

        account = new Account();
        account.setAccountId(1L);
        account.setAccountNumber("12345");
        account.setAccountType("Checking");
        account.setAccountDescription("Personal Checking Account");
        account.setBalance(new BigDecimal("500.00"));
        account.setVersion(1);
        account.setCustomer(customer);

        customer.setAccounts(Collections.singletonList(account));

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testFindAccounts_Success() {
        // Arrange
        List<Account> accountList = Collections.singletonList(account);
        Page<Account> accountPage = new PageImpl<>(accountList, pageable, accountList.size());
        when(accountRepository.findAccounts(anyLong(), anyString(), anyString(), eq(pageable))).thenReturn(accountPage);
        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));

        // Act
        Page<CustomerDTO> result = accountService.findAccounts(1L, "12345", "Checking", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        CustomerDTO customerDTO = result.getContent().get(0);
        assertEquals(customer.getCustomerId(), customerDTO.customerId());
        assertEquals(customer.getName(), customerDTO.name());
        assertEquals(customer.getEmail(), customerDTO.email());
        assertEquals(customer.getPhone(), customerDTO.phone());
        assertEquals(1, customerDTO.accounts().size());

        AccountDTO accountDTO = customerDTO.accounts().get(0);
        assertEquals(account.getAccountId(), accountDTO.accountId());
        assertEquals(account.getAccountNumber(), accountDTO.accountNumber());
        assertEquals(account.getAccountType(), accountDTO.accountType());
        assertEquals(account.getAccountDescription(), accountDTO.accountDescription());
        assertEquals(account.getBalance(), accountDTO.balance());

        verify(accountRepository).findAccounts(anyLong(), anyString(), anyString(), eq(pageable));
        verify(customerRepository).findById(customer.getCustomerId());
    }

    @Test
    void testFindAccounts_NoCustomerFound() {
        // Arrange
        List<Account> accountList = Collections.singletonList(account);
        Page<Account> accountPage = new PageImpl<>(accountList, pageable, accountList.size());
        when(accountRepository.findAccounts(anyLong(), anyString(), anyString(), eq(pageable))).thenReturn(accountPage);
        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.empty());

        // Act
        Page<CustomerDTO> result = accountService.findAccounts(1L, "12345", "Checking", 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());

        verify(accountRepository).findAccounts(anyLong(), anyString(), anyString(), eq(pageable));
        verify(customerRepository).findById(customer.getCustomerId());
    }

    @Test
    void testUpdateAccountDescription_Success() {
        // Arrange
        String newDescription = "Updated Checking Account Description";
        Account updatedAccount = new Account();
        updatedAccount.setAccountId(account.getAccountId());
        updatedAccount.setAccountNumber(account.getAccountNumber());
        updatedAccount.setAccountType(account.getAccountType());
        updatedAccount.setAccountDescription(newDescription);
        updatedAccount.setBalance(account.getBalance());
        updatedAccount.setVersion(account.getVersion());
        updatedAccount.setCustomer(account.getCustomer());

        when(accountRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(updatedAccount);

        // Act
        AccountDTO result = accountService.updateAccountDescription(account.getAccountId(), newDescription);

        // Assert
        assertNotNull(result);
        assertEquals(newDescription, result.accountDescription());
        assertEquals(account.getAccountId(), result.accountId());

        verify(accountRepository).findById(account.getAccountId());
        verify(accountRepository).save(account);
    }

    @Test
    void testUpdateAccountDescription_AccountNotFound() {
        // Arrange
        String newDescription = "Updated Checking Account Description";
        when(accountRepository.findById(account.getAccountId())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.updateAccountDescription(account.getAccountId(), newDescription);
        });
        assertEquals("Account not found", exception.getMessage());

        verify(accountRepository).findById(account.getAccountId());
    }
}