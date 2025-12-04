package com.example.fooddonation.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation")
public class DonationDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Original fields
    private String donationType;
    private String foodName;
    private String mealType;
    private String category;
    private String quantity;
    private LocalDateTime donatedDate;
    private LocalDateTime expiryDateTime;
    private String city;
    private String amount;
    private String clothesType;
    private String itemName;

    // ✅ NEW FIELDS FOR STATUS TRACKING
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50) DEFAULT 'PENDING'")
    private DonationStatus status;

    // Timestamps for each status
    private LocalDateTime confirmedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime pickupScheduledDate;  // When pickup is scheduled for
    private LocalDateTime pickedUpAt;
    private LocalDateTime inTransitAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime completedAt;

    // Additional tracking information
    @Column(length = 500)
    private String pickupAddress;
    
    @Column(columnDefinition = "TEXT")
    private String specialInstructions;
    
    @Column(columnDefinition = "TEXT")
    private String ngoNotes;
    
    @Column(length = 255)
    private String statusMessage;
    
    @Column(length = 100)
    private String updatedBy;
    
    private Integer beneficiariesCount;
    
    @Column(columnDefinition = "TEXT")
    private String impactDescription;
    
    @Column(columnDefinition = "LONGTEXT")
    private String proofImageBase64;  // Store image as base64

    @Column(length = 100)
    private String proofImageName;    // Original filename

    @Column(length = 50)
    private String proofImageType;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "ngo_id")
    @JsonIgnoreProperties({"donations"})
    private NgoDTO ngo;

    @ManyToOne
    @JoinColumn(name = "donor_id")
    @JsonIgnoreProperties({"donations"})
    private DonorDTO donor;

    @PrePersist
    public void setTimestamp() {
        this.donatedDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = DonationStatus.PENDING;
        }
    }

    // Default Constructor
    public DonationDTO() {}

    // ========== ORIGINAL GETTERS & SETTERS ==========

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDonationType() {
        return donationType;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getDonatedDate() {
        return donatedDate;
    }

    public void setDonatedDate(LocalDateTime donatedDate) {
        this.donatedDate = donatedDate;
    }

    public LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getClothesType() {
        return clothesType;
    }

    public void setClothesType(String clothesType) {
        this.clothesType = clothesType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public NgoDTO getNgo() {
        return ngo;
    }

    public void setNgo(NgoDTO ngo) {
        this.ngo = ngo;
    }

    public DonorDTO getDonor() {
        return donor;
    }

    public void setDonor(DonorDTO donor) {
        this.donor = donor;
    }

    // ✅ NEW GETTERS & SETTERS FOR STATUS TRACKING

    public DonationStatus getStatus() {
        return status;
    }

    public void setStatus(DonationStatus status) {
        this.status = status;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getPickupScheduledDate() {
        return pickupScheduledDate;
    }

    public void setPickupScheduledDate(LocalDateTime pickupScheduledDate) {
        this.pickupScheduledDate = pickupScheduledDate;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(LocalDateTime pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public LocalDateTime getInTransitAt() {
        return inTransitAt;
    }

    public void setInTransitAt(LocalDateTime inTransitAt) {
        this.inTransitAt = inTransitAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getNgoNotes() {
        return ngoNotes;
    }

    public void setNgoNotes(String ngoNotes) {
        this.ngoNotes = ngoNotes;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getBeneficiariesCount() {
        return beneficiariesCount;
    }

    public void setBeneficiariesCount(Integer beneficiariesCount) {
        this.beneficiariesCount = beneficiariesCount;
    }

    public String getImpactDescription() {
        return impactDescription;
    }

    public void setImpactDescription(String impactDescription) {
        this.impactDescription = impactDescription;
    }
    
    public String getProofImageBase64() {
        return proofImageBase64;
    }

    public void setProofImageBase64(String proofImageBase64) {
        this.proofImageBase64 = proofImageBase64;
    }

    public String getProofImageName() {
        return proofImageName;
    }

    public void setProofImageName(String proofImageName) {
        this.proofImageName = proofImageName;
    }

    public String getProofImageType() {
        return proofImageType;
    }

    public void setProofImageType(String proofImageType) {
        this.proofImageType = proofImageType;
    }
}