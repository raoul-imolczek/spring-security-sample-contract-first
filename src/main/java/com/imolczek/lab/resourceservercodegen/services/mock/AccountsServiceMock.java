package com.imolczek.lab.resourceservercodegen.services.mock;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.imolczek.lab.resourceservercodegen.exceptions.WrongAccountHolderException;
import com.imolczek.lab.resourceservercodegen.model.Account;
import com.imolczek.lab.resourceservercodegen.services.AccountsService;

@Service
public class AccountsServiceMock implements AccountsService {

	@Override
	public List<Account> list(String accountHolderId) {
		
		List<Account> accounts = new ArrayList<>();

		Account accountA = new Account();
		accountA.setAccountNumber("12345");
		accounts.add(accountA);
		
		Account accountB = new Account();
		accountB.setAccountNumber("ABCDE");
		accounts.add(accountB);
		
		return accounts;
	}

	@Override
	public Account details(String accountNumber, String accountHolderId) throws WrongAccountHolderException {

		if(accountNumber.endsWith("9")) throw new WrongAccountHolderException();
		
		Account account = new Account();
		account.setAccountNumber(accountNumber);
		
		return account;
	}

}
