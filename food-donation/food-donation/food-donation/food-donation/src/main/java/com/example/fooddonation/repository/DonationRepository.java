package com.example.fooddonation.repository;

import com.example.fooddonation.entity.DonationDTO;
import com.example.fooddonation.entity.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<DonationDTO, Integer> {

    // ✅ YOUR EXISTING QUERIES (KEEP THESE - They handle lazy loading properly)
    @Query("SELECT d FROM DonationDTO d LEFT JOIN FETCH d.donor LEFT JOIN FETCH d.ngo WHERE d.donor.id = :donorId ORDER BY d.id DESC")
    List<DonationDTO> findByDonorId(@Param("donorId") int donorId);

    @Query("SELECT d FROM DonationDTO d LEFT JOIN FETCH d.donor LEFT JOIN FETCH d.ngo WHERE d.ngo.id = :ngoId ORDER BY d.id DESC")
    List<DonationDTO> findByNgoId(@Param("ngoId") int ngoId);

    // ✅ NEW METHODS FOR STATUS TRACKING (ADD THESE)
    
    // Find by donor ordered by donation date
    @Query("SELECT d FROM DonationDTO d LEFT JOIN FETCH d.donor LEFT JOIN FETCH d.ngo WHERE d.donor.id = :donorId ORDER BY d.donatedDate DESC")
    List<DonationDTO> findByDonorIdOrderByDonatedDateDesc(@Param("donorId") int donorId);
    
    // Find by NGO ordered by donation date
    @Query("SELECT d FROM DonationDTO d LEFT JOIN FETCH d.donor LEFT JOIN FETCH d.ngo WHERE d.ngo.id = :ngoId ORDER BY d.donatedDate DESC")
    List<DonationDTO> findByNgoIdOrderByDonatedDateDesc(@Param("ngoId") int ngoId);
    
    // Find by status
    @Query("SELECT d FROM DonationDTO d LEFT JOIN FETCH d.donor LEFT JOIN FETCH d.ngo WHERE d.status = :status")
    List<DonationDTO> findByStatus(@Param("status") DonationStatus status);
    
    // Find by NGO and status
    @Query("SELECT d FROM DonationDTO d LEFT JOIN FETCH d.donor LEFT JOIN FETCH d.ngo WHERE d.ngo.id = :ngoId AND d.status = :status")
    List<DonationDTO> findByNgoIdAndStatus(@Param("ngoId") int ngoId, @Param("status") DonationStatus status);
    
    // Find by donor and status
    @Query("SELECT d FROM DonationDTO d LEFT JOIN FETCH d.donor LEFT JOIN FETCH d.ngo WHERE d.donor.id = :donorId AND d.status = :status")
    List<DonationDTO> findByDonorIdAndStatus(@Param("donorId") int donorId, @Param("status") DonationStatus status);
    
    @Query("SELECT d FROM DonationDTO d WHERE d.donor.id = :donorId")
    List<DonationDTO> findByDonorFull(@Param("donorId") int donorId);

}