package edu.asu.sbs.services;

import java.math.BigDecimal;
import java.util.List;

import edu.asu.sbs.model.Account;
import edu.asu.sbs.model.Transaction;

public interface AccountService {

	List<Account> getAccountByCustomerId(int customerId);
	Account getAccountByNumber(int accountNumber);
	public void updateAccount(Account account);
	public BigDecimal getBalance(int accountNumber);
	void transferFunds(Transaction sender, Transaction receiver,BigDecimal amount);
	public boolean approveTransaction(Transaction transaction);
}
