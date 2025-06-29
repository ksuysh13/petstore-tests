package com.example.petstore.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class OrderApi extends BaseApi {
    private static final String STORE_ENDPOINT = "/store/order";
    
    public Response createOrder(String orderJson) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(orderJson)
                .post(STORE_ENDPOINT);
    }
    
    public Response getOrderById(long orderId) {
        return RestAssured.given()
                .get(STORE_ENDPOINT + "/" + orderId);
    }
    
    public Response getInventory() {
        return RestAssured.given()
                .get("/store/inventory");
    }
    
    public Response deleteOrder(long orderId) {
        return RestAssured.given()
                .delete(STORE_ENDPOINT + "/" + orderId);
    }
}