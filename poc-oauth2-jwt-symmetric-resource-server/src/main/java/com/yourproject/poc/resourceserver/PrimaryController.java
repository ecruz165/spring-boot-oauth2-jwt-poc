package com.yourproject.poc.resourceserver;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class PrimaryController {
	
	@PreAuthorize("#oauth2.hasScope('fooScope') and hasAnyRole('ROLE_ANONYMOUS'")
	@RequestMapping("/")
	@ResponseBody
	public String pleaseSignIn() {
		return "If you are Authenticated, try to visit <a href='/public'>/public</a> and/or <a href='/private'>/private</a>  \n";
	}
	
	@PreAuthorize("#oauth2.hasScope('fooScope') and hasAnyRole('ROLE_USER','ROLE_ADMIN') \n")
	@RequestMapping("/public")
	@ResponseBody
	public String welcome(Principal principal) {
		return principal == null ? "Welcome anonymous" : "Hello " + principal.getName() +"\n";
	}
	
	@PreAuthorize("#oauth2.hasScope('fooScope') and hasRole('ROLE_ADMIN')")
	@RequestMapping("/private")
	@ResponseBody
	public String welcomeVIP(Principal principal) {
		return "Hello " + principal.getName() +"\n";
	}

}