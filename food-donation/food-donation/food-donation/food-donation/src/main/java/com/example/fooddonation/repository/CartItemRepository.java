package com.example.fooddonation.repository;

import com.example.fooddonation.entity.CartItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemDTO, Integer> { }
