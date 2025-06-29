package com.example.petstore.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UserApi extends BaseApi {
    private static final String USER_ENDPOINT = "/user";
    
    public Response createUser(String userJson) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .post(USER_ENDPOINT);
    }
    
    public Response getUserByUsername(String username) {
        return RestAssured.given()
                .get(USER_ENDPOINT + "/" + username);
    }
    
    public Response updateUser(String username, String userJson) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(userJson)
                .put(USER_ENDPOINT + "/" + username);
    }
    
    public Response userLogin(String username, String password) {
        return RestAssured.given()
                .queryParam("username", username)
                .queryParam("password", password)
                .get(USER_ENDPOINT + "/login");
    }
    
    public Response userLogout() {
        return RestAssured.given()
                .get(USER_ENDPOINT + "/logout");
    }
    
    public Response createUsersWithList(String usersJson) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(usersJson)
                .post(USER_ENDPOINT + "/createWithList");
    }
    
    public Response createUsersWithArray(String usersJson) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(usersJson)
                .post(USER_ENDPOINT + "/createWithArray");
    }
    
    public Response deleteUser(String username) {
        return RestAssured.given()
                .delete(USER_ENDPOINT + "/" + username);
    }
}