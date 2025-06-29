package com.example.petstore.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.*;

public class UserStoreApiTest {
    private UserApi userApi;
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

    public UserStoreApiTest() {
        userApi = new UserApi();
    }

    @Test
    @DisplayName("Create user")
    public void testCreateUser() {
        Response response = userApi.createUser(userJson);
        assertEquals(200, response.getStatusCode(), "Failed to create user");
        userApi.deleteUser(username);
    }

    @Test
    @DisplayName("Get user by username")
    public void testGetUserByUsername() {
        userApi.createUser(userJson);
        Response response = userApi.getUserByUsername(username);
        assertEquals(200, response.getStatusCode());
        assertEquals(username, response.jsonPath().getString("username"), "User not found");
        userApi.deleteUser(username);
    }

    @Test
    @DisplayName("Update user")
    public void testUpdateUser() {
        userApi.createUser(userJson);
        Response response = userApi.updateUser(username, updatedUserJson);
        assertEquals(200, response.getStatusCode());

        Response getResponse = userApi.getUserByUsername(username);
        assertEquals("Updated", getResponse.jsonPath().getString("firstName"));
        userApi.deleteUser(username);
    }

    @Test
    @DisplayName("User login")
    public void testUserLogin() {
        userApi.createUser(userJson);
        Response response = userApi.userLogin(username, "test123");
        assertEquals(200, response.getStatusCode());
        assertTrue(response.jsonPath().getString("message").contains("logged in user session"));
        userApi.deleteUser(username);
    }

    @Test
    @DisplayName("User logout")
    public void testUserLogout() {
        Response response = userApi.userLogout();
        assertEquals(200, response.getStatusCode());
        assertTrue(response.jsonPath().getString("message").contains("ok"));
    }

    @Test
    @DisplayName("Create users with list")
    public void testCreateUsersWithList() {
        Response response = userApi.createUsersWithList(usersJson);
        assertEquals(200, response.getStatusCode(), "Failed to create users with list");
        userApi.deleteUser(user1);
        userApi.deleteUser(user2);
    }

    @Test
    @DisplayName("Create users with array")
    public void testCreateUsersWithArray() {
        Response response = userApi.createUsersWithArray(usersJson);
        assertEquals(200, response.getStatusCode(), "Failed to create users with array");
        userApi.deleteUser(user1);
        userApi.deleteUser(user2);
    }
}