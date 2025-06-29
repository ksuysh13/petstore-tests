package com.example.petstore.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserStoreApiTest {
    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private static final String USER_ENDPOINT = "/user";
    private static String username = "testuser123";
    private static String user1 = "user1";
    private static String user2 = "user2";

    private static String userJson = """
                {
                    "id": 0,
                    "username": "%s",
                    "firstName": "Test",
                    "lastName": "User",
                    "email": "test@example.com",
                    "password": "test123",
                    "phone": "1234567890",
                    "userStatus": 1
                }
                """.formatted(username);

    private static String updatedUserJson = """
                {
                    "id": 0,
                    "username": "%s",
                    "firstName": "Updated",
                    "lastName": "User",
                    "email": "updated@example.com",
                    "password": "updated123",
                    "phone": "0987654321",
                    "userStatus": 1
                }
                """.formatted(username);

    private static String usersJson = """
            [
                {
                    "id": 0,
                    "username": "%s",
                    "firstName": "User1",
                    "lastName": "Test",
                    "email": "user1@example.com",
                    "password": "pass1",
                    "phone": "1111111111",
                    "userStatus": 1
                },
                {
                    "id": 0,
                    "username": "%s",
                    "firstName": "User2",
                    "lastName": "Test",
                    "email": "user2@example.com",
                    "password": "pass2",
                    "phone": "2222222222",
                    "userStatus": 1
                }
            ]
            """.formatted(user1, user2);

    private static void createUser() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .post(USER_ENDPOINT);

        assertEquals(200, response.getStatusCode(), "Failed to create user");
        System.out.println("Create user");
    }

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("Create user")
    public void testCreateUser() {
        createUser();

        RestAssured.given().delete(USER_ENDPOINT + "/" + username);
    }

    @Test
    @DisplayName("Get user by username")
    public void testGetUserByUsername() {
        createUser();
        
        Response response = RestAssured.given()
                .get(USER_ENDPOINT + "/" + username);

        assertEquals(200, response.getStatusCode());
        assertEquals(username, response.jsonPath().getString("username"), "User not found");

        RestAssured.given().delete(USER_ENDPOINT + "/" + username);
    }

    @Test
    @DisplayName("Update user")
    public void testUpdateUser() {
        createUser();

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updatedUserJson)
                .put(USER_ENDPOINT + "/" + username);

        assertEquals(200, response.getStatusCode());

        Response getResponse = RestAssured.given()
                .get(USER_ENDPOINT + "/" + username);
        assertEquals("Updated", getResponse.jsonPath().getString("firstName"));

        RestAssured.given().delete(USER_ENDPOINT + "/" + username);
    }

    @Test
    @DisplayName("User login")
    public void testUserLogin() {
        createUser();
        
        Response response = RestAssured.given()
                .queryParam("username", username)
                .queryParam("password", "test123")
                .get(USER_ENDPOINT + "/login");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.jsonPath().getString("message").contains("logged in user session"));
    }

    @Test
    @DisplayName("User logout")
    public void testUserLogout() {
        Response response = RestAssured.given()
                .get(USER_ENDPOINT + "/logout");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.jsonPath().getString("message").contains("ok"));
    }

    @Test
    @DisplayName("Create users with list")
    public void testCreateUsersWithList() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(usersJson)
                .post(USER_ENDPOINT + "/createWithList");

        assertEquals(200, response.getStatusCode(), "Failed to create users with list");

        RestAssured.given().delete(USER_ENDPOINT + "/" + user1);
        RestAssured.given().delete(USER_ENDPOINT + "/" + user2);
    }

    @Test
    @DisplayName("Create users with array")
    public void testCreateUsersWithArray() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(usersJson)
                .post(USER_ENDPOINT + "/createWithArray");

        assertEquals(200, response.getStatusCode(), "Failed to create users with array");

        RestAssured.given().delete(USER_ENDPOINT + "/" + user1);
        RestAssured.given().delete(USER_ENDPOINT + "/" + user2);
    }
}
