package com.example.petstore.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

public class PetStoreApiTest {
    private PetApi petApi;
    private Long petId;
    private static String testNameDog = "Buddy";
    private static String updateNameDog = "Max";
    
    private static String petJson = """
            {
                "id": 0,
                "category": {
                    "id": 1,
                    "name": "dogs"
                },
                "name": "%s",
                "photoUrls": ["src/test/resources/Buddy.jpg"],
                "tags": [
                    {
                        "id": 1,
                        "name": "friendly"
                    }
                ],
                "status": "available"
            }
            """.formatted(testNameDog);

    @BeforeEach
    public void setUp() {
        petApi = new PetApi();
        Response response = petApi.createPet(petJson);
        petId = response.jsonPath().getLong("id");
        assertEquals(200, response.getStatusCode(), "Failed to create pet");
        System.out.println("Created pet with ID: " + petId);
    }

    @Test
    @DisplayName("Find pet by ID")
    public void testGetPetById() {
        Response response = petApi.getPetById(petId);
        assertEquals(200, response.getStatusCode(), "Failed to get pet by ID");
        assertEquals(testNameDog, response.jsonPath().getString("name"), "Pet name mismatch");
        assertEquals("available", response.jsonPath().getString("status"), "Pet status mismatch");
    }

    @Test
    @DisplayName("Update an existing pet")
    public void testUpdatePet() {
        String updatedPetJson = """
            {
                "id": %d,
                "category": {
                    "id": 1,
                    "name": "dogs"
                },
                "name": "%s",
                "photoUrls": ["src/test/resources/test_image.jpg"],
                "tags": [
                    {
                        "id": 1,
                        "name": "friendly"
                    }
                ],
                "status": "sold"
            }
            """.formatted(petId, updateNameDog);
            
        Response response = petApi.updatePet(updatedPetJson);
        assertEquals(200, response.getStatusCode(), "Failed to update pet");
        assertEquals(updateNameDog, response.jsonPath().getString("name"), "Updated pet name mismatch");
        assertEquals("sold", response.jsonPath().getString("status"), "Updated pet status mismatch");
    }

    @Test
    @DisplayName("Find pets by status")
    public void testFindPetsByStatus() {
        Response response = petApi.findPetsByStatus("available");
        assertEquals(200, response.getStatusCode(), "Failed to find pets by status");
        List<Object> pets = response.jsonPath().getList("$");
        assertFalse(pets.isEmpty(), "No pets found with status 'available'");
    }

    @Test
    @DisplayName("Updates a pet in the store with form data")
    public void testUpdatePetWithFormData() {
        Response response = petApi.updatePetWithFormData(petId, "Rex", "pending");
        assertEquals(200, response.getStatusCode());
        
        Response getResponse = petApi.getPetById(petId);
        assertEquals("Rex", getResponse.jsonPath().getString("name"));
        assertEquals("pending", getResponse.jsonPath().getString("status"));
    }

    @Test
    @DisplayName("Uploads an image")
    public void testUploadPetImage() {
        File imageFile = new File("src/test/resources/test_image.jpg");
        Response response = petApi.uploadPetImage(petId, imageFile);
        assertEquals(200, response.getStatusCode());
        assertTrue(response.jsonPath().getString("message").contains("File uploaded"));
    }

    @AfterEach
    @DisplayName("Deletes a pet")
    public void tearDown() {
        if (petId != null) {
            Response response = petApi.deletePet(petId);
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