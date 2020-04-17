package com.imolczek.lab.resourceservercodegen.security;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TenantRepository {

	@Value("${security.oauth2.resourceserver.issuer}") String issuerUri;
	@Value("${security.oauth2.resourceserver.jwk.jwk-set-uri}") String jwkSetUri;

	@Value("${security.oauth2bis.resourceserver.issuer}") String issuerUriBis;
	@Value("${security.oauth2bis.resourceserver.jwk.jwk-set-uri}") String jwkSetUriBis;

	private Map<String, Tenant> tenants;
	
	@PostConstruct
	public void init() {
		tenants = new HashMap<>();
		
		Tenant oauth2 = new Tenant();
		oauth2.setIssuer(issuerUri);
		oauth2.setJwksUri(jwkSetUri);
		tenants.put(issuerUri, oauth2);
	
		Tenant oauth2bis = new Tenant();
		oauth2bis.setIssuer(issuerUriBis);
		oauth2bis.setJwksUri(jwkSetUriBis);
		tenants.put(issuerUriBis, oauth2bis);

	}
	
	public Tenant findById(String tenant) {
		return tenants.get(tenant);
	}

}
