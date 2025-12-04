package com.example.fooddonation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fooddonation.entity.NgoDTO;
import com.example.fooddonation.service.NgoService;

@RestController
@RequestMapping("/ngo")
@CrossOrigin(origins = "*")
public class NgoController {

    @Autowired
    private NgoService ngoService;

    @PostMapping("/add")
    public ResponseEntity<?> addNgo(@RequestBody NgoDTO ngo) {
        try {
            return ResponseEntity.status(201).body(ngoService.addNgo(ngo));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<NgoDTO>> getAllNgos() {
        return ResponseEntity.ok(ngoService.getAllNgos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNgoById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(ngoService.getNgoById(id));
        } catch (Exception ex) {
            return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNgo(@PathVariable int id, @RequestBody NgoDTO ngo) {
        try {
            return ResponseEntity.ok(ngoService.updateNgo(id, ngo));
        } catch (Exception ex) {
            return ResponseEntity.status(400).body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNgo(@PathVariable int id) {
        ngoService.deleteNgo(id);
        return ResponseEntity.ok(Map.of("message", "NGO deleted successfully"));
    }
}
