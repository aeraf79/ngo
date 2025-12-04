package com.example.fooddonation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fooddonation.entity.DonationDTO;
import com.example.fooddonation.service.DonationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/donation")
@CrossOrigin(origins = "*")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @PostMapping("/add/{donorId}")
    public ResponseEntity<?> addDonation(@PathVariable int donorId, @RequestBody DonationDTO payload) {
        try {
            DonationDTO saved = donationService.addDonation(donorId, payload);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/update/{id}/{donorId}")
    public ResponseEntity<?> updateDonation(@PathVariable int id, @PathVariable int donorId, @RequestBody DonationDTO payload) {
        try {
            DonationDTO updated = donationService.updateDonation(id, donorId, payload);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<DonationDTO>> getAllDonations() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<?> getByDonor(@PathVariable int donorId) {
        return ResponseEntity.ok(donationService.getByDonor(donorId));
    }

    @GetMapping("/ngo/{ngoId}")
    public ResponseEntity<?> getByNgo(@PathVariable int ngoId) {
        return ResponseEntity.ok(donationService.getByNgo(ngoId));
    }
}
