package com.example.petstore.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

public class OrderStoreApiTest {
    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private static final String STORE_ENDPOINT = "/store/order";
    private Long orderId;

    private static 
        String orderJson = """
                {
                    "id": 0,
                    "petId": 1,
                    "quantity": 1,
                    "shipDate": "2025-06-28T09:08:35.889Z",
                    "status": "placed",
                    "complete": true
                }
                """;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = BASE_URL;

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(orderJson)
                .post(STORE_ENDPOINT);

        orderId = response.jsonPath().getLong("id");
        assertEquals(200, response.getStatusCode(), "Failed to create order");
        System.out.println("Created order with ID: " + orderId);
    }
    
    @Test
    @DisplayName("Find purchase order by ID")
    public void testGetOrderById() {        
        Response response = RestAssured.given()
                .get(STORE_ENDPOINT + "/" + orderId);

        // Проверки
        assertEquals(200, response.getStatusCode(), "Failed to get order by ID");
        assertEquals("placed", response.jsonPath().getString("status"), "Order status mismatch");
    }

    @Test
    @DisplayName("Get pet inventories by status - check response structure and values")
    public void testGetInventory() {
        Response response = RestAssured.given()
                .get("/store/inventory");

        // Проверка статус кода
        assertEquals(200, response.getStatusCode(), "Failed to get inventory");
        
        // Проверка структуры ответа
        Map<String, Object> inventory = response.jsonPath().getMap("");
        assertFalse(inventory.isEmpty(), "Inventory should not be empty");
        System.out.println(inventory);
        
        // проверка наличия хотя бы одного известного статуса
        List<String> possibleKnownStatuses = List.of("available", "sold", "pending");
        boolean hasKnownStatus = inventory.keySet().stream()
                .anyMatch(possibleKnownStatuses::contains);
        assertTrue(hasKnownStatus, 
            "Response should contain at least one known status from: " + possibleKnownStatuses);
    }

    @AfterEach
    @DisplayName("Delete purchase order by ID")
    public void tearDown() {
        // Удаление заказа после каждого теста
        if (orderId != null) {
            Response response = RestAssured.given()
                    .delete(STORE_ENDPOINT + "/" + orderId);

            assertEquals(200, response.getStatusCode(), "Failed to delete order");
            System.out.println("Deleted order with ID: " + orderId);
        }

        try {
        Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
