package org.limckmy.account.repository;

import org.limckmy.account.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Custom query method using @Query
    @Query("SELECT a FROM Account a WHERE (:customerId IS NULL OR a.customer.customerId = :customerId) " +
            "AND (:accountNumber IS NULL OR a.accountNumber = :accountNumber) " +
            "AND (:description IS NULL OR a.accountDescription LIKE %:description%)")
    Page<Account> findAccounts(@Param("customerId") Long customerId,
                               @Param("accountNumber") String accountNumber,
                               @Param("description") String description,
                               Pageable pageable);
}
