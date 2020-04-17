package com.imolczek.lab.resourceservercodegen.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.imolczek.lab.resourceservercodegen.security.MyIDPAuthoritiesConverter;
import com.imolczek.lab.resourceservercodegen.security.TenantJWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;

/**
 * See documentation here: https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html#oauth2ResourceServer-org.springframework.security.config.Customizer-
 * The prePostEnabled attribute is what allows me to use @PreAuthorize annotations in the REST
 * controller to authorize requests depending on the scope or the roles 
 * @author Fabian Bouché
 *
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OAuth2ResourceServerSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//http.authorizeRequests().anyRequest().permitAll();
		http
			.cors()
			.and().oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter())
		;
		
	}

	/**
	 * The way scopes and roles are defined in JWT tokens is not 100% standard
	 * Thus, you'll often have to specify your own conversion rules
	 * What we mean here with conversion, is how we map claims in JWT tokens
	 * as "Authorities" bound to the security context
	 * The mapped Authorities can be used in the @PreAuthorize annotations
	 * inside of the REST Controller
	 * @return the JwtAuthenticationConverter
	 */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new MyIDPAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }	
    

    @Bean
    JWTProcessor jwtProcessor(TenantJWSKeySelector keySelector) {
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor =
                new DefaultJWTProcessor();
        jwtProcessor.setJWTClaimsSetAwareJWSKeySelector(keySelector);
        return jwtProcessor;
    }

    /**
     * Provision of a JwtDecoder bean that uses the IDP configuration from properties to
     * decode the JWT access tokens provided alongside requests
     * @return A JWTDecoder
     */
	@Bean
	JwtDecoder jwtDecoder(JWTProcessor processor, OAuth2TokenValidator<Jwt> jwtValidator) {

	    NimbusJwtDecoder decoder = new NimbusJwtDecoder(processor);
	    OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>
	            (JwtValidators.createDefault(), jwtValidator);
	    decoder.setJwtValidator(validator);
	    return decoder;
		
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3200"));
		configuration.setAllowedMethods(Arrays.asList("GET","POST"));
		configuration.setAllowedHeaders(Arrays.asList("authorization"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
