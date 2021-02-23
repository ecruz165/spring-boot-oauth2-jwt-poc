package com.yourproject.poc.resourceserver;

import static org.junit.Assert.assertEquals;

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

	@Autowired
	Environment env;

	private String serverURL = null;

	@Before
	public void defineURLs() {
		this.serverURL = "http://localhost:" + env.getProperty("local.server.port");
	}

	@Test
	public void whenLoadApplication_thenSuccess() {
	}

	/**
	 * REQUEST UNPROTECTED PAGE ON RESOURCE SERVER 
	 * curl localhost:8082/ anonumous or
	 * other can reach this reasource
	 */
	@Test
	public void whenRequestOpenPage_thenOK() {
		// obtain first access token
		final Response value = requestURL(serverURL);
		assertEquals(200, value.getStatusCode());
	}

	/**
	 * REQUEST PROTECTED PAGE ON RESOURCE SERVER 
	 * curl localhost:8082/ only roles of
	 * user or admin can reach this page
	 */
	@Test
	public void whenRequestUserPage_thenUnauthorized() {
		// obtain first access token
		final Response value = requestURL(serverURL + "/public");
		assertEquals(401, value.getStatusCode());
	}

	/**
	 * ANONYMOUS REQUEST PROTECTED PAGE ON RESOURCE SERVER 
	 * curl localhost:8082/private 
	 * only role of admin can readh this page
	 */
	@Test
	public void whenRequestAdminPage_thenUnauthorized() {
		final Response value = requestURL(serverURL + "/private");
		assertEquals(401, value.getStatusCode());
	}

	private Response requestURL(final String url) {
		Response response = RestAssured.given().log().all().when().get(url).prettyPeek();
		return response;
	}
}