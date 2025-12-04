package com.example.fooddonation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.example.fooddonation.entity.DonorDTO;
import com.example.fooddonation.service.DonorService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/donor")
public class DonorController {

    @Autowired
    private DonorService ds;

    /**
     * Get all donors
     */
    @GetMapping("/all")
    public ResponseEntity<List<DonorDTO>> getAll() {
        List<DonorDTO> donors = ds.getall();
        return ResponseEntity.ok(donors);
    }

    /**
     * Get donor by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        Optional<DonorDTO> donor = ds.getbyid(id);
        
        if (donor.isPresent()) {
            return ResponseEntity.ok(donor.get());
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Donor not found with ID: " + id));
    }

    /**
     * Register new donor with validation
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody DonorDTO donor, BindingResult bindingResult) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }

            // Set default role if not provided
            if (donor.getRole() == null || donor.getRole().isEmpty()) {
                donor.setRole("DONOR");
            }

            // Check if email already exists
            DonorDTO existingDonor = ds.getByEmail(donor.getEmail());
            if (existingDonor != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Email already registered. Please use a different email."));
            }

            // Additional custom validation
            if (!donor.isEmailValid()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("email", "Please provide a valid email address"));
            }

            if (!donor.isPhoneValid()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("phone", "Phone number must be exactly 10 digits"));
            }

            // Register the donor
            String message = ds.addnewuser(donor);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "message", message,
                        "success", true
                    ));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Registration failed: " + ex.getMessage()));
        }
    }

    /**
     * Login with validation
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DonorDTO loginData) {
        try {
            // Validate input
            if (loginData.getEmail() == null || loginData.getEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Email is required"));
            }

            if (loginData.getPassword() == null || loginData.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Password is required"));
            }

            // Validate email format
            if (!loginData.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Please provide a valid email address"));
            }

            // Authenticate user
            DonorDTO user = ds.authenticate(loginData.getEmail(), loginData.getPassword());
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid email or password"));
            }

            return ResponseEntity.ok(user);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed: " + ex.getMessage()));
        }
    }

    /**
     * Delete donor by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            Optional<DonorDTO> donor = ds.getbyid(id);
            
            if (!donor.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Donor not found with ID: " + id));
            }

            ds.deletebyid(id);
            
            return ResponseEntity.ok(Map.of(
                "message", "Donor deleted successfully",
                "id", id
            ));
            
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete donor: " + ex.getMessage()));
        }
    }

    /**
     * Full update (PUT) with validation
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> putUpdate(@PathVariable int id, @Valid @RequestBody DonorDTO donor, 
                                       BindingResult bindingResult) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }

            DonorDTO updated = ds.updatebyput(id, donor);
            return ResponseEntity.ok(updated);
            
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * Partial update (PATCH) - validation optional on fields
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchUpdate(@PathVariable int id, @RequestBody DonorDTO donor) {
        try {
            // Validate only provided fields
            Map<String, String> errors = new HashMap<>();

            if (donor.getName() != null && !donor.getName().isEmpty()) {
                if (!donor.getName().matches("^[a-zA-Z\\s]+$")) {
                    errors.put("name", "Name can only contain letters and spaces");
                }
                if (donor.getName().length() < 2) {
                    errors.put("name", "Name must be at least 2 characters");
                }
            }

            if (donor.getEmail() != null && !donor.getEmail().isEmpty()) {
                if (!donor.isEmailValid()) {
                    errors.put("email", "Please provide a valid email address");
                }
            }

            if (donor.getPhone() != null && !donor.getPhone().isEmpty()) {
                if (!donor.isPhoneValid()) {
                    errors.put("phone", "Phone must be exactly 10 digits");
                }
            }

            if (donor.getPassword() != null && !donor.getPassword().isEmpty()) {
                if (donor.getPassword().length() < 6) {
                    errors.put("password", "Password must be at least 6 characters");
                }
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }

            DonorDTO updated = ds.updatebypatch(id, donor);
            
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Donor not found with ID: " + id));
            }

            return ResponseEntity.ok(updated);
            
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update donor: " + ex.getMessage()));
        }
    }

    
    
}