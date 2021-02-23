package com.yourproject.poc.authserver;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class AppTest {

	private static final Logger log = LoggerFactory.getLogger(AppTest.class);

	private static String ACCESS_TOKEN_KEY = "access_token";
	private static String REFRESH_TOKEN_KEY = "refresh_token";

	@Autowired
	Environment env;

	private String accessTokenURL = null;


	@Before
	public void defineURLs() {
		final String accessTokenUrl = "http://localhost:" + env.getProperty("local.server.port");
		final String path = "/oauth/token";
		this.accessTokenURL = accessTokenUrl + path;
	}

	@Test
	public void whenLoadApplication_thenSuccess() {
	}

	/***
	 * OBTAIN ACCESS TOKEN USING CURL: 
	 * 		curl fooClient:fooSecret@localhost:8081/oauth/token 
	 * 		-d grant_type=password 
	 * 		-d username=user 
	 * 		-d password=password
	 * 
	 * NOTE: curl automatically base64 encodes clientId and clientSecret and places value in 
	 * Authorization Header. An Alternative is:
	 * 
	 * curl localhost:8081/oauth/token 
	 * 		-H "Authorization: Basic `echo -n clientId:clientSecret | base64`" 
	 * 		-d grant_type=password 
	 * 		-d username=user 
	 * 		-d password=password
	 */
	@Test
	public void whenObtainingAccessToken_thenOK() {
		final Response authServerResponse = obtainAccessToken("fooClient", "fooSecret", "user", "password");
		final String accessToken = authServerResponse.jsonPath().getString(ACCESS_TOKEN_KEY);
		assertNotNull(accessToken);
		final String refreshToken = authServerResponse.jsonPath().getString(REFRESH_TOKEN_KEY);
		assertNotNull(refreshToken);
	}

	/** 
	 * OBTAIN A NEW ACCESS TOKEN USING REFRESH TOKEN REQUEST
	 * curl localhost:8081/oauth/token 
	 * 		-H "Authorization: Basic={Base64_Encoded_ClientId:ClientSecret} 
	 * 		-d grant_type=password 
	 * 		-d username=user 
	 * 		-d password=password
	 */
	@Test
	public void whenObtainingRenewedAccessToken_thenOK() {
		// obtain first access token
		final Response authServerResponse = obtainAccessToken("fooClient", "fooSecret", "user", "password");
		final String accessToken1 = authServerResponse.jsonPath().getString(ACCESS_TOKEN_KEY);
		// confirm first access token is not null
		assertNotNull(accessToken1);
		final String refreshToken = authServerResponse.jsonPath().getString(REFRESH_TOKEN_KEY);
		// confirm refresh token is not null
		assertNotNull(refreshToken);
		// make refresh token request to get new access token
		final Response authServerResponse2 = refreshAccessToken("fooClient", "fooSecret", refreshToken);
		final String accessToken2 = authServerResponse2.jsonPath().getString(ACCESS_TOKEN_KEY);
		// confirm new access token is not null
		assertNotNull(accessToken2);
		// confirm its a new token
		assertTrue(!accessToken1.equals(accessToken2));
	}

	
	/** 
	 * CHECK ACCESS TOKEN
	 * curl localhost:8081/check/token 
	 * 		-H "Authorization: Basic={Base64_Encoded_ClientId:ClientSecret} 
	 * 		-d token=dsflnsdfnsdfowienfowen 
	 */
	@Test
	public void whenCheckToken_thenOK() {
		// obtain first access token
		final Response authServerResponse = obtainAccessToken("fooClient", "fooSecret", "user", "password");
		final String accessToken1 = authServerResponse.jsonPath().getString(ACCESS_TOKEN_KEY);
		// confirm first access token is not null
		assertNotNull(accessToken1);
		final String refreshToken = authServerResponse.jsonPath().getString(REFRESH_TOKEN_KEY);
		// confirm refresh token is not null
		assertNotNull(refreshToken);
		// make refresh token request to get new access token
		final Response authServerResponse2 = refreshAccessToken("fooClient", "fooSecret", refreshToken);
		final String accessToken2 = authServerResponse2.jsonPath().getString(ACCESS_TOKEN_KEY);
		// confirm new access token is not null
		assertNotNull(accessToken2);
		// confirm its a new token
		assertTrue(!accessToken1.equals(accessToken2));
	}
	
	private Response obtainAccessToken(final String clientId, final String clientSecret, final String username, final String password) {
		printHR("OBTAIN ACCESS TOKEN");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "password");
		params.put("username", username);
		params.put("password", password);
		Response response = RestAssured
			.given().log().all()
				.auth().preemptive().basic(clientId, clientSecret)
			.and().with()
				.params(params)
			.when()
				.post(accessTokenURL).prettyPeek();
		return response;
	}

	private Response refreshAccessToken(final String clientId, final String clientSecret, final String refreshToken) {
		printHR("REFRESH NEW ACCESS TOKEN");
		final Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", REFRESH_TOKEN_KEY);
		params.put(REFRESH_TOKEN_KEY, refreshToken);
		Response response = RestAssured
			.given().log().all()
				.auth().preemptive().basic(clientId, clientSecret)
			.and().with()
				.params(params)
			.when()
				.post(accessTokenURL).prettyPeek();
		return response;
	}

	private void printHR(String title) {
		StringBuffer buf = new StringBuffer();
		final String hr = StringUtils.repeat("*", 60);
		buf.append("\n").append(hr).append("\n").append(hr).append("\n").append(hr).append("\n").append(title);
		log.info(buf.toString());
	}
}