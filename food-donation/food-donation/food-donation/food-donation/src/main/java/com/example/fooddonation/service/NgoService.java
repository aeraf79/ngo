package com.example.fooddonation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fooddonation.entity.NgoDTO;
import com.example.fooddonation.repository.NgoRepository;

@Service
public class NgoService {

    @Autowired
    private NgoRepository ngoRepository;

    public NgoDTO addNgo(NgoDTO ngo) {
        return ngoRepository.save(ngo);
    }

    public List<NgoDTO> getAllNgos() {
        return ngoRepository.findAll();
    }

    public NgoDTO getNgoById(int id) {
        return ngoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NGO not found with ID: " + id));
    }

    public NgoDTO updateNgo(int id, NgoDTO updatedNgo) {
        NgoDTO existingNgo = getNgoById(id);

        existingNgo.setNgoName(updatedNgo.getNgoName());
        existingNgo.setEmail(updatedNgo.getEmail());
        existingNgo.setPhone(updatedNgo.getPhone());
        existingNgo.setAddress(updatedNgo.getAddress());
        existingNgo.setCity(updatedNgo.getCity());
        existingNgo.setState(updatedNgo.getState());
        existingNgo.setImageUrl(updatedNgo.getImageUrl());
        existingNgo.setFoundationYear(updatedNgo.getFoundationYear());
        existingNgo.setDescription(updatedNgo.getDescription());

        return ngoRepository.save(existingNgo);
    }

    public void deleteNgo(int id) {
        ngoRepository.deleteById(id);
    }
}
