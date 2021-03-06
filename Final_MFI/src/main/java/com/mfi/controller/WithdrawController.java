package com.mfi.controller;

import java.sql.Date;
import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.mfi.model.COA;
import com.mfi.model.CurrentAccount;
import com.mfi.model.Transaction;
import com.mfi.service.CoaAccountService;
import com.mfi.service.CurrentAccountService;
import com.mfi.service.TransactionService;

@Controller
public class WithdrawController {

	@Autowired
	TransactionService transactionService;
	@Autowired
	CurrentAccountService currentAccountService;
	@Autowired
	CoaAccountService coaService;
	@PostMapping("/withdraw")
	public String deposit(@ModelAttribute("withdrawBean")@Valid Transaction transaction , BindingResult result, Model model) {
		if(result.hasErrors()) {
			model.addAttribute("transaction", transaction);
			return "mfi/transaction/MFI_WTD_01";
		}
		LocalDate now = LocalDate.now();
		Date date = Date.valueOf(now);
//		get amount
		Double amount = transaction.getAmount();
		String accountNo=transaction.getAccountNumber();
		CurrentAccount currentAcc=currentAccountService.getAccountNumber(accountNo);
		
		if(currentAcc == null) {
			model.addAttribute("transaction", transaction);
			model.addAttribute("errorMessage","Account number does not exist");
			return "mfi/transaction/MFI_WTD_01";
			
		}
		
		CurrentAccount currentAccount = currentAccountService.getAccountNumber(transaction.getAccountNumber());
		Double balance = currentAccount.getBalance()-amount;
		currentAccount.setBalance(balance);
		currentAccount.setUpdateDate(date);
		currentAccountService.update(currentAccount);
		
		COA coa = coaService.getCOA("Cash");
		Double coaBalance= coa.getAmount()-amount;
		coa.setAmount(coaBalance);
		coa.setUpdateDate(date);
		coaService.update(coa);
		
		
//		transaction.setDate(date);
		transaction.setTransactionType("Withdraw");
		transaction.setCoaId(5);
		transaction.setCreatedDate(date);
		transactionService.deposit(transaction);
		
		Transaction bankTran = new Transaction();
		bankTran.setTransactionType("Cash");
		bankTran.setAccountName("Bank Cash Account");
		bankTran.setCoaId(coa.getCoaId());
		bankTran.setAccountNumber(coa.getAccountNumber());
		bankTran.setAmount(amount);
		bankTran.setNarration("Customer Withdraw");
		bankTran.setCreatedDate(date);
		transactionService.bankTran(bankTran);
		model.addAttribute("withdrawBean", new Transaction());
		model.addAttribute("reg",true);
		return "mfi/transaction/MFI_WTD_01";
	}
	
}