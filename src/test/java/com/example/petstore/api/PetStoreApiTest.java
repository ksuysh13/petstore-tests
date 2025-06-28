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

import java.util.List;

public class PetStoreApiTest {

    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private static final String PET_ENDPOINT = "/pet";
    private Long petId; // ID созданного питомца

    @BeforeEach
    @DisplayName("Add a new pet to the store")
    public void setUp() {
        // Настройка базового URL для всех запросов
        RestAssured.baseURI = BASE_URL;

        // Создание тестового питомца перед каждым тестом
        String petJson = """
                {
                    "id": 0,
                    "category": {
                        "id": 1,
                        "name": "dogs"
                    },
                    "name": "Buddy",
                    "photoUrls": ["https://example.com/buddy.jpg"],
                    "tags": [
                        {
                            "id": 1,
                            "name": "friendly"
                        }
                    ],
                    "status": "available"
                }
                """;

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .post(PET_ENDPOINT);

        // Сохраняем ID созданного питомца
        petId = response.jsonPath().getLong("id");
        System.out.println("Created pet with ID: " + petId);
    }

    @Test
    @DisplayName("Find pet by ID")
    public void testGetPetById() {
        // Получение информации о питомце по ID
        Response response = RestAssured.given()
                .get(PET_ENDPOINT + "/" + petId);

        // Проверки
        assertEquals(200, response.getStatusCode(), "Failed to get pet by ID");
        assertEquals("Buddy", response.jsonPath().getString("name"), "Pet name mismatch");
        assertEquals("available", response.jsonPath().getString("status"), "Pet status mismatch");
    }

    @Test
    @DisplayName("Update an existing pet")
    public void testUpdatePet() {
        // Обновление информации о питомце
        String updatedPetJson = """
                {
                    "id": %d,
                    "category": {
                        "id": 1,
                        "name": "dogs"
                    },
                    "name": "Max",
                    "photoUrls": ["https://example.com/max.jpg"],
                    "tags": [
                        {
                            "id": 1,
                            "name": "friendly"
                        }
                    ],
                    "status": "sold"
                }
                """.formatted(petId);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updatedPetJson)
                .put(PET_ENDPOINT);

        // Проверки
        assertEquals(200, response.getStatusCode(), "Failed to update pet");
        assertEquals("Max", response.jsonPath().getString("name"), "Updated pet name mismatch");
        assertEquals("sold", response.jsonPath().getString("status"), "Updated pet status mismatch");
    }

    @Test
    @DisplayName("Find pets by status")
    public void testFindPetsByStatus() {
        Response response = RestAssured.given()
                .queryParam("status", "available")
                .get(PET_ENDPOINT + "/findByStatus");

        assertEquals(200, response.getStatusCode(), "Failed to find pets by status");
        List<Object> pets = response.jsonPath().getList("$");
        assertFalse(pets.isEmpty(), "No pets found with status 'available'");
    }

    @Test
    @DisplayName("Updates a pet in the store with form data")
    public void testUpdatePetWithFormData() {
        Response response = RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam("name", "Rex")
                .formParam("status", "pending")
                .post(PET_ENDPOINT + "/" + petId);

        assertEquals(200, response.getStatusCode());
        
        // Проверяем, что обновление применилось
        Response getResponse = RestAssured.given()
                .get(PET_ENDPOINT + "/" + petId);
        
        assertEquals("Rex", getResponse.jsonPath().getString("name"));
        assertEquals("pending", getResponse.jsonPath().getString("status"));
    }

    @AfterEach
    @DisplayName("Deletes a pet")
    public void tearDown() {
        // Удаление питомца после каждого теста
        if (petId != null) {
            Response response = RestAssured.given()
                    .delete(PET_ENDPOINT + "/" + petId);

            assertEquals(200, response.getStatusCode(), "Failed to delete pet");
            System.out.println("Deleted pet with ID: " + petId);
        }

        try {
        Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    
}
