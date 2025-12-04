package com.example.fooddonation.entity;

import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "donor")
public class DonorDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be exactly 10 digits")
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    private String address;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(DONOR|ADMIN|USER)$", message = "Role must be DONOR, ADMIN, or USER")
    private String role;

    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"donations"})
    private List<DonationDTO> donations;

    // Default Constructor
    public DonorDTO() {}

    // Getters & Setters
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        // Trim whitespace and validate
        this.name = (name != null) ? name.trim() : null; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        // Convert to lowercase and trim
        this.email = (email != null) ? email.trim().toLowerCase() : null; 
    }

    public String getPhone() { 
        return phone; 
    }
    
    public void setPhone(String phone) { 
        // Remove any non-digit characters
        if (phone != null) {
            this.phone = phone.replaceAll("[^0-9]", "");
        } else {
            this.phone = null;
        }
    }

    public String getAddress() { 
        return address; 
    }
    
    public void setAddress(String address) { 
        this.address = (address != null) ? address.trim() : null; 
    }

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }

    public String getCity() { 
        return city; 
    }
    
    public void setCity(String city) { 
        this.city = (city != null) ? city.trim() : null; 
    }

    public String getState() { 
        return state; 
    }
    
    public void setState(String state) { 
        this.state = (state != null) ? state.trim() : null; 
    }

    public String getRole() { 
        return role; 
    }
    
    public void setRole(String role) { 
        // Convert to uppercase for consistency
        this.role = (role != null) ? role.trim().toUpperCase() : null; 
    }

    public List<DonationDTO> getDonations() { 
        return donations; 
    }
    
    public void setDonations(List<DonationDTO> donations) { 
        this.donations = donations; 
    }

    // Utility method to check if email is valid format
    public boolean isEmailValid() {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // Utility method to check if phone is valid format
    public boolean isPhoneValid() {
        if (phone == null) return false;
        return phone.matches("^[0-9]{10}$");
    }

    @Override
    public String toString() {
        return "DonorDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}