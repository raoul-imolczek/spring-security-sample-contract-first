package com.imolczek.lab.resourceservercodegen.security;

public class Tenant {
	
	private String issuer;
	private String jwksUri;

	public String getIssuer() {
		return issuer;
	}
	
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	public String getJwksUri() {
		return jwksUri;
	}
	
	public void setJwksUri(String jwksUri) {
		this.jwksUri = jwksUri;
	}

}
