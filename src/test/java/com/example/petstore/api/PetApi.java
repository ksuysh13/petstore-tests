package com.example.petstore.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.io.File;

public class PetApi extends BaseApi {
    private static final String PET_ENDPOINT = "/pet";
    
    public Response createPet(String petJson) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .post(PET_ENDPOINT);
    }
    
    public Response getPetById(long petId) {
        return RestAssured.given()
                .get(PET_ENDPOINT + "/" + petId);
    }
    
    public Response updatePet(String petJson) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(petJson)
                .put(PET_ENDPOINT);
    }
    
    public Response findPetsByStatus(String status) {
        return RestAssured.given()
                .queryParam("status", status)
                .get(PET_ENDPOINT + "/findByStatus");
    }
    
    public Response updatePetWithFormData(long petId, String name, String status) {
        return RestAssured.given()
                .contentType(ContentType.URLENC)
                .formParam("name", name)
                .formParam("status", status)
                .post(PET_ENDPOINT + "/" + petId);
    }
    
    public Response uploadPetImage(long petId, File imageFile) {
        return RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .multiPart("file", imageFile, "image/jpeg")
                .post(PET_ENDPOINT + "/" + petId + "/uploadImage");
    }
    
    public Response deletePet(long petId) {
        return RestAssured.given()
                .delete(PET_ENDPOINT + "/" + petId);
    }
}