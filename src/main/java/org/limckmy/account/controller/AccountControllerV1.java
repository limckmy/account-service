package org.limckmy.account.controller;


import org.limckmy.account.dto.CustomerDTO;
import org.limckmy.account.dto.UpdateAccountDescriptionDTO;
import org.limckmy.account.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
public class AccountControllerV1 {

    private final AccountService accountService;

    public AccountControllerV1(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("")
    public ResponseEntity<?> findAccount(@RequestParam(value = "customerId", required = false) Long customerId,
                                         @RequestParam(value = "accountNumber", required = false) String accountNumber,
                                         @RequestParam(value = "description", required = false) String description,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<CustomerDTO> customerDTOList = accountService.findAccounts(customerId, accountNumber, description, page, size);
        if (customerDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(customerDTOList);
    }

    @PatchMapping("/{accountId}/description")
    public ResponseEntity<?> updateAccountDescription(@PathVariable("accountId") Long accountId,
                                                      @RequestBody UpdateAccountDescriptionDTO request) {
        return ResponseEntity.ok(accountService.updateAccountDescription(accountId, request.description()));
    }

}
