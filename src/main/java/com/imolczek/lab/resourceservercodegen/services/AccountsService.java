package com.imolczek.lab.resourceservercodegen.services;

import java.util.List;

import com.imolczek.lab.resourceservercodegen.exceptions.WrongAccountHolderException;
import com.imolczek.lab.resourceservercodegen.model.Account;

public interface AccountsService {

	public List<Account> list(String accountHolderId);

	public Account details(String accountNumber, String accountHolderId) throws WrongAccountHolderException;
	
}
