package com.example.petstore.api;

import io.restassured.RestAssured;

public class BaseApi {
    protected static final String BASE_URL = "https://petstore.swagger.io/v2";
    
    public BaseApi() {
        RestAssured.baseURI = BASE_URL;
    }
}
