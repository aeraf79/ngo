package com.example.fooddonation.repository;

import com.example.fooddonation.entity.NgoDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NgoRepository extends JpaRepository<NgoDTO, Integer> { }
