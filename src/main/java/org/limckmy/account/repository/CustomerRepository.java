package org.limckmy.account.repository;

import org.limckmy.account.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c JOIN c.accounts a WHERE c.customerId = :customerId AND a.accountId = :accountId")
    Optional<Customer> findByCustomerIdAndAccountId(@Param("customerId") Long customerId,@Param("accountId") Long accountId);
}
