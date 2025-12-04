package com.example.fooddonation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.fooddonation.entity.FeedbackDTO;

public interface FeedbackRepository extends JpaRepository<FeedbackDTO, Integer> {}
