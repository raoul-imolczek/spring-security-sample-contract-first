package com.imolczek.lab.resourceservercodegen.interfaces.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.imolczek.lab.resourceservercodegen.exceptions.WrongAccountHolderException;
import com.imolczek.lab.resourceservercodegen.interfaces.rest.resources.AccountResource;
import com.imolczek.lab.resourceservercodegen.interfaces.rest.resources.Whoami;
import com.imolczek.lab.resourceservercodegen.model.Account;
import com.imolczek.lab.resourceservercodegen.services.AccountsService;

@RestController
public class SampleApiController implements AccountsApi, PingApi, WhoamiApi {

	private static Logger logger = LoggerFactory.getLogger(SampleApiController.class);

	private AccountsService accountsService;
	
	public SampleApiController(AccountsService accountsService) {
		this.accountsService = accountsService;
	}
	
	@Override
	@PreAuthorize("permitAll()")
	public ResponseEntity<String> pingUsingGET() {
		return new ResponseEntity<String>("pong", HttpStatus.OK);
	}

	@Override
	@PreAuthorize("hasAuthority('SCOPE_accounts:details') and hasAuthority('ROLE_customer')")
	public ResponseEntity<AccountResource> accountDetailsUsingGET(Jwt jwt, SecurityContext context,
			String accountNumber) {
		
		String accountHolderId = jwt.getClaimAsString("sub");
		
		Account account;
		try {
			account = accountsService.details(accountNumber, accountHolderId);
		} catch (WrongAccountHolderException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong account holder", e);
		}
		
		AccountResource accountResource = new AccountResource();
		accountResource.setAccountNumber(account.getAccountNumber());

		return new ResponseEntity<AccountResource>(accountResource, HttpStatus.OK);
	}

	@Override
	@PreAuthorize("hasAuthority('SCOPE_accounts:list') and hasAuthority('ROLE_customer')")
	public ResponseEntity<List<AccountResource>> accountsUsingGET(Jwt jwt, SecurityContext context) {

		List<AccountResource> accountResourceList = new ArrayList<>();
		
		String accountHolderId = jwt.getClaimAsString("sub");
		Iterator<Account> accountsIterator = accountsService.list(accountHolderId).iterator();
		
		while(accountsIterator.hasNext()) {
			Account account = accountsIterator.next();
			AccountResource accountResource = new AccountResource();
			accountResource.setAccountNumber(account.getAccountNumber());
			
			accountResourceList.add(accountResource );
		}

		return new ResponseEntity<List<AccountResource>>(accountResourceList, HttpStatus.OK);
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Whoami> whoamiUsingGET(Jwt jwt, SecurityContext context) {

		Whoami whoami = new Whoami();

		Iterator<? extends GrantedAuthority> authorities = context.getAuthentication().getAuthorities().iterator();
		
		whoami.setFirstName(jwt.getClaimAsString("given_name"));
		whoami.setLastName(jwt.getClaimAsString("family_name"));

		List<String> roles = new ArrayList<>();
		while(authorities.hasNext()) {
			roles.add(authorities.next().getAuthority());
		}
		whoami.setRoles(roles);
		
		return new ResponseEntity<Whoami>(whoami, HttpStatus.OK);
	}	
}
