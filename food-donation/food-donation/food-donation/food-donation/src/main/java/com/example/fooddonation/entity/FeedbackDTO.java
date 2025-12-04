package com.example.fooddonation.entity;

import jakarta.persistence.*;

@Entity
@Table(name="feedback")
public class FeedbackDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String email;
    private String message;

    public FeedbackDTO() {}

    public FeedbackDTO(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int  id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
