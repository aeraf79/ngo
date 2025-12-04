package com.example.fooddonation.repository;

import com.example.fooddonation.entity.CartDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<CartDTO, Integer> {

    @Query("SELECT c FROM CartDTO c WHERE c.donor.id = :donorId AND c.status = 'PENDING' ORDER BY c.id DESC")
    CartDTO findPendingCartByDonor(@Param("donorId") int donorId);
}
