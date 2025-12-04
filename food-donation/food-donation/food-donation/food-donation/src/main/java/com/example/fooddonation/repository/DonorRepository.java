package com.example.fooddonation.repository;

import com.example.fooddonation.entity.DonorDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonorRepository extends JpaRepository<DonorDTO, Integer> {
    DonorDTO findByEmail(String email);
}
