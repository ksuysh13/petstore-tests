package com.example.petstore.models;

import java.util.List;

import lombok.Data;

@Data
public class Pet {
    private Long id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private List<Tag> tags;
    private String status;
}
