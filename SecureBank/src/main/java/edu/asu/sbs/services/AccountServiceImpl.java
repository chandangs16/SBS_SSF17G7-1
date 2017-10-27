package edu.asu.sbs.services;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.sound.midi.Receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.sbs.dao.AccountDAO;
import edu.asu.sbs.model.Account;
import edu.asu.sbs.model.Transaction;

@Service
@Transactional
public class AccountServiceImpl implements AccountService{

	@Autowired
	AccountDAO accountDAO;
	
	@Override
	public List<Account> getAccountByCustomerId(int customerId) {
		// TODO Auto-generated method stub
		return accountDAO.findByCustomerID(customerId);
	}

	@Override
	public Account getAccountByNumber(int accountNumber) {
		// TODO Auto-generated method stub
		return accountDAO.findByAccountNumber(accountNumber);
	}

	@Override
	public void updateAccount(Account account) {
		// TODO Auto-generated method stub
		accountDAO.updateAccount(account);
		
	}

	@Override
	public BigDecimal getBalance(int accountNumber) {
		// TODO Auto-generated method stub
		return accountDAO.getBalance(accountNumber);
	}

	@Override
	public void transferFunds(Transaction sender, Transaction receiver, BigDecimal amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Account findByAccountNumber(int i) {
		// TODO Auto-generated method stub
		return accountDAO.findByAccountNumber(i);
	}
	
	public void transferFunds(TransactionService transactionService,
			AccountService accountService, Transaction senderTransaction,
			Transaction receiverTransaction, double amount) {
		
		Account senderAccount = accountService.getAccountByNumber(senderTransaction.getSenderAccNumber());
		Account receiverAccount = accountService.getAccountByNumber(senderTransaction.getReceiverAccNumber());
		System.out.println("senderAccount: " + senderAccount);
		System.out.println("receiverAccount: " + receiverAccount);
		
		// update account balances
		senderAccount.setAccountBalance(senderAccount.getAccountBalance() - amount);
		receiverAccount.setAccountBalance(receiverAccount.getAccountBalance() + amount);
		
		System.out.println("senderAccount after updating balance: " + senderAccount);
		System.out.println("receiverAccount after updating balance: " + receiverAccount);
		
		// create transactions
		transactionService.addTransaction(senderTransaction);
		transactionService.addTransaction(receiverTransaction);

	}
}