package com.mfi.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mfi.model.LoanAccount;
import com.mfi.model.LoanInfo;
import com.mfi.model.SavingAccount;

public interface SavingAccountRepository extends JpaRepository<SavingAccount, String> {
	
	@Query("select s from SavingAccount s where s.customer.customerCode=?1") 
	  SavingAccount findbyCode(String code);
	
	@Query("select s from SavingAccount s where s.createdDate between ?1 and ?2")
	List<SavingAccount> accountFilter(Date start, Date end);

}
