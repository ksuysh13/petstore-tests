package com.example.petstore.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class OrderStoreApiTest {
    private OrderApi orderApi;
    private Long orderId;
    
    private static String orderJson = """
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
        orderApi = new OrderApi();
        Response response = orderApi.createOrder(orderJson);
        orderId = response.jsonPath().getLong("id");
        assertEquals(200, response.getStatusCode(), "Failed to create order");
        System.out.println("Created order with ID: " + orderId);
    }
    
    @Test
    @DisplayName("Find purchase order by ID")
    public void testGetOrderById() {
        Response response = orderApi.getOrderById(orderId);
        assertEquals(200, response.getStatusCode(), "Failed to get order by ID");
        assertEquals("placed", response.jsonPath().getString("status"), "Order status mismatch");
    }

    @Test
    @DisplayName("Get pet inventories by status - check response structure and values")
    public void testGetInventory() {
        Response response = orderApi.getInventory();
        assertEquals(200, response.getStatusCode(), "Failed to get inventory");
        
        Map<String, Object> inventory = response.jsonPath().getMap("");
        assertFalse(inventory.isEmpty(), "Inventory should not be empty");
        System.out.println(inventory);
        
        List<String> possibleKnownStatuses = List.of("available", "sold", "pending");
        boolean hasKnownStatus = inventory.keySet().stream()
                .anyMatch(possibleKnownStatuses::contains);
        assertTrue(hasKnownStatus, 
            "Response should contain at least one known status from: " + possibleKnownStatuses);
    }

    @AfterEach
    @DisplayName("Delete purchase order by ID")
    public void tearDown() {
        if (orderId != null) {
            Response response = orderApi.deleteOrder(orderId);
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