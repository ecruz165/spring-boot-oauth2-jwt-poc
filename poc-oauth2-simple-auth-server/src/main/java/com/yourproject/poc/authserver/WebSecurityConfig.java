package com.yourproject.poc.authserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig  extends WebSecurityConfigurerAdapter {
    
	@Autowired
	public void globalUserDetails(final AuthenticationManagerBuilder auth) throws Exception { // @formatter:off
		auth.inMemoryAuthentication()
			.withUser("user").password(passwordEncoder().encode("password")).roles("USER")
				.and()
			.withUser("admin").password(passwordEncoder().encode("password")).roles("ADMIN");
	}
	
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return  new BCryptPasswordEncoder();
    }
    
	@Bean
	@Override
	 public AuthenticationManager authenticationManagerBean() throws Exception {
	      return super.authenticationManagerBean();
	}  
}
