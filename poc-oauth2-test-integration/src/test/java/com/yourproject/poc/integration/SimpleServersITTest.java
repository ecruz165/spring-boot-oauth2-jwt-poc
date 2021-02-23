package com.yourproject.poc.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.yourproject.test.integration.IntegrationTestConfig;
import com.yourproject.test.integration.IntegrationTestServerManager;
import com.yourproject.test.oauth2.helper.OAuthRequestHelper;

import io.restassured.response.Response;

@SpringJUnitConfig( classes = { IntegrationTestConfig.class })
@TestInstance(Lifecycle.PER_CLASS)
@TestPropertySource("classpath:application-test.properties")
@DisplayName("Microserice Integrations Tests For Simple Servers")
public class SimpleServersITTest  {
	
	@Autowired
	IntegrationTestConfig config;
	IntegrationTestServerManager manager;
	OAuthRequestHelper helper;
	
	@BeforeAll
	void setUp() throws Exception {
		helper = new OAuthRequestHelper();
		manager = new IntegrationTestServerManager(config);
		manager.startServers(SimpleServersITTest.class.getSimpleName());
	}

	@AfterAll
	void tearDown() {
		manager.stopServers(SimpleServersITTest.class.getSimpleName());		
	}

	@Test
	@DisplayName("ANONYMOUS VISITS OPEN PAGE")
	void requestPublicAsUserTest() throws Exception {
		Response response  = helper.accessResource("http://localhost:8082/", "footClient", "fooSecret");
		assertEquals(200, response.getStatusCode());
	}

	@Test
	@DisplayName("ANONYMOUS VISITS PAGE FOR USERS and ADMIN")
	void requestPublicAsAdminTest() {
		Response accessToken= helper.obtainAccessToken("http://localhost:8081/oauth/token", "password", "fooClient", "fooSecret", "user", "password");
		Response response  = helper.accessResource("http://localhost:8082/public", "footClient", "fooSecret");
		assertEquals(401, response.getStatusCode());
	}

	@Test
	@DisplayName("USER LOGINS AND VISITS PAGE FOR USERS OR ADMINS")
	void requestPrivateAsUserTest() {
		Response accessTokenResponse = helper.obtainAccessToken("http://localhost:8081/oauth/token", "password", "fooClient", "fooSecret", "user", "password");
		String accessToken=null;
		try {
			accessToken = new JSONObject(accessTokenResponse.print()).getString("access_token");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		Response response  = helper.accessResource("http://localhost:8082/public", accessToken);
		assertEquals(200, response.getStatusCode());
	}

}
