package com.example.fooddonation.service;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fooddonation.entity.DonorDTO;
import com.example.fooddonation.repository.DonorRepository;

@Service
public class DonorService {

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private EmailService emailService;

    public List<DonorDTO> getall() {
        return donorRepository.findAll();
    }

    public Optional<DonorDTO> getbyid(int id) {
        return donorRepository.findById(id);
    }

    public String deletebyid(int id) {
        donorRepository.deleteById(id);
        return "Deleted Successfully";
    }

    public DonorDTO updatebyput(int id, DonorDTO dd) {
        Optional<DonorDTO> o = donorRepository.findById(id);
        if (o.isPresent()) {
            DonorDTO p = o.get();
            p.setAddress(dd.getAddress());
            p.setCity(dd.getCity());
            p.setEmail(dd.getEmail());
            p.setName(dd.getName());
            p.setPhone(dd.getPhone());
            p.setState(dd.getState());
            return donorRepository.save(p);
        }
        throw new RuntimeException("Donor not found");
    }

    public DonorDTO updatebypatch(int id, DonorDTO dd) {
        Optional<DonorDTO> o = donorRepository.findById(id);
        if (o.isPresent()) {
            DonorDTO existing = o.get();

            if (dd.getName() != null) existing.setName(dd.getName());
            if (dd.getEmail() != null) existing.setEmail(dd.getEmail());
            if (dd.getPhone() != null) existing.setPhone(dd.getPhone());
            if (dd.getAddress() != null) existing.setAddress(dd.getAddress());
            if (dd.getCity() != null) existing.setCity(dd.getCity());
            if (dd.getState() != null) existing.setState(dd.getState());

            // No hashing — store plain text password
            if (dd.getPassword() != null && !dd.getPassword().isBlank()) {
                existing.setPassword(dd.getPassword());
            }

            return donorRepository.save(existing);
        }
        return null;
    }

    public DonorDTO getByEmail(String email) {
        if (email == null) return null;
        return donorRepository.findByEmail(email);
    }

    // Register new user (save plain password)
    public String addnewuser(@Valid DonorDTO e) {
        if (e.getEmail() == null || e.getPassword() == null) {
            throw new RuntimeException("Email and password required");
        }

        // Save raw password directly
        donorRepository.save(e);

        try {
            String subject = "Welcome to Our NGO Community!";
            String message = "Dear " + e.getName() + ",\n\n" +
                    "Thank you for registering with our NGO. We appreciate your interest in joining our community and supporting our mission.\n\n" +
                    "We look forward to keeping you updated on our initiatives and opportunities to get involved.\n\n" +
                    "Best regards,\nThe NGO Team";

            emailService.sendEmail(e.getEmail(), subject, message);
        } catch (Exception ex) {
            System.out.println("Email failed: " + ex.getMessage());
        }

        return "User registered";
    }

    // Authenticate (raw password check)
    public DonorDTO authenticate(String email, String password) {
        DonorDTO user = donorRepository.findByEmail(email);
        if (user == null) return null;

        if (!user.getPassword().equals(password)) return null;

        return user;
    }
}
