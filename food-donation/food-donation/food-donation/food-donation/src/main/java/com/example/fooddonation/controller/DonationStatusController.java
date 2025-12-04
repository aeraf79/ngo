// ===== DonationStatusController.java - UPDATED WITH EMAIL NOTIFICATIONS =====
package com.example.fooddonation.controller;

import com.example.fooddonation.entity.DonationDTO;
import com.example.fooddonation.entity.DonationStatus;
import com.example.fooddonation.repository.DonationRepository;
import com.example.fooddonation.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/donation/status")
@CrossOrigin(origins = "*")
public class DonationStatusController {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private EmailService emailService;

    // Get all donations for a donor with status
    @GetMapping("/donor/{donorId}")
    public ResponseEntity<?> getDonorDonations(@PathVariable int donorId) {
        try {
            List<DonationDTO> donations = donationRepository.findByDonorIdOrderByDonatedDateDesc(donorId);
            return ResponseEntity.ok(donations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all donations for an NGO (for NGO dashboard)
    @GetMapping("/ngo/{ngoId}")
    public ResponseEntity<?> getNgoDonations(@PathVariable int ngoId) {
        try {
            List<DonationDTO> donations = donationRepository.findByNgoIdOrderByDonatedDateDesc(ngoId);
            return ResponseEntity.ok(donations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get donations by status (for NGO to see pending pickups, etc.)
    @GetMapping("/ngo/{ngoId}/status/{status}")
    public ResponseEntity<?> getNgoDonationsByStatus(
            @PathVariable int ngoId,
            @PathVariable String status
    ) {
        try {
            DonationStatus donationStatus = DonationStatus.valueOf(status.toUpperCase());
            List<DonationDTO> donations = donationRepository.findByNgoIdAndStatus(ngoId, donationStatus);
            return ResponseEntity.ok(donations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get single donation with full details
    @GetMapping("/{donationId}")
    public ResponseEntity<?> getDonationDetails(@PathVariable int donationId) {
        try {
            DonationDTO donation = donationRepository.findById(donationId)
                    .orElseThrow(() -> new RuntimeException("Donation not found"));
            return ResponseEntity.ok(donation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update donation status (Main endpoint) - WITH EMAIL NOTIFICATION
    @PutMapping("/update/{donationId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable int donationId,
            @RequestBody Map<String, Object> payload
    ) {
        try {
            DonationDTO donation = donationRepository.findById(donationId)
                    .orElseThrow(() -> new RuntimeException("Donation not found"));

            String newStatusStr = (String) payload.get("status");
            DonationStatus newStatus = DonationStatus.valueOf(newStatusStr.toUpperCase());

            // Validate status progression (can't go backwards)
            if (donation.getStatus() != null && newStatus.isBefore(donation.getStatus())) {
                throw new RuntimeException("Cannot move status backwards");
            }

            LocalDateTime now = LocalDateTime.now();
            DonationStatus oldStatus = donation.getStatus();

            // Update status and set timestamp
            donation.setStatus(newStatus);

            switch (newStatus) {
                case CONFIRMED:
                    donation.setConfirmedAt(now);
                    donation.setStatusMessage("Donation confirmed. NGO will contact you soon.");
                    break;

                case SCHEDULED:
                    donation.setScheduledAt(now);
                    // Get scheduled date from payload
                    String scheduledDateStr = (String) payload.get("pickupScheduledDate");
                    if (scheduledDateStr != null) {
                        donation.setPickupScheduledDate(LocalDateTime.parse(scheduledDateStr));
                    }
                    String instructions = (String) payload.get("specialInstructions");
                    if (instructions != null) {
                        donation.setSpecialInstructions(instructions);
                    }
                    donation.setStatusMessage("Pickup scheduled successfully.");
                    break;

                case PICKED_UP:
                    donation.setPickedUpAt(now);
                    donation.setStatusMessage("Donation picked up from donor.");
                    break;

                case IN_TRANSIT:
                    donation.setInTransitAt(now);
                    donation.setStatusMessage("Donation is being transported to distribution center.");
                    break;

                case DELIVERED:
                    donation.setDeliveredAt(now);
                    Integer beneficiariesCount = (Integer) payload.get("beneficiariesCount");
                    if (beneficiariesCount != null) {
                        donation.setBeneficiariesCount(beneficiariesCount);
                    }
                    donation.setStatusMessage("Donation delivered to beneficiaries.");
                    break;

                 // Replace the COMPLETED case in your updateStatus method with this:

                case COMPLETED:
                    donation.setCompletedAt(now);
                    Integer completedBeneficiaries = (Integer) payload.get("beneficiariesCount");
                    if (completedBeneficiaries != null) {
                        donation.setBeneficiariesCount(completedBeneficiaries);
                    }
                    
                    // Handle proof image
                    String proofImageBase64 = (String) payload.get("proofImageBase64");
                    String proofImageName = (String) payload.get("proofImageName");
                    String proofImageType = (String) payload.get("proofImageType");
                    
                    if (proofImageBase64 != null && !proofImageBase64.isEmpty()) {
                        donation.setProofImageBase64(proofImageBase64);
                        donation.setProofImageName(proofImageName);
                        donation.setProofImageType(proofImageType);
                        System.out.println("✅ Proof image saved: " + proofImageName);
                    }
                    
                    donation.setStatusMessage("Donation delivered to beneficiaries.");
                    
                    // ✅ SEND COMPLETION EMAIL WITH IMAGE
                    sendCompletionEmailWithImage(donation);
                    break;
            }

            // Add NGO notes if provided
            String ngoNotes = (String) payload.get("ngoNotes");
            if (ngoNotes != null && !ngoNotes.trim().isEmpty()) {
                donation.setNgoNotes(ngoNotes);
            }

            // Track who updated
            String updatedBy = (String) payload.get("updatedBy");
            if (updatedBy != null) {
                donation.setUpdatedBy(updatedBy);
            }

            // Custom message override
            String customMessage = (String) payload.get("statusMessage");
            if (customMessage != null && !customMessage.trim().isEmpty()) {
                donation.setStatusMessage(customMessage);
            }

            donationRepository.save(donation);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("donation", donation);
            response.put("message", "Status updated to " + newStatus.getDisplayName());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status: " + e.getMessage());
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ NEW METHOD: Send completion email
 // Add this method to DonationStatusController.java
 // Replace the existing sendCompletionEmail method with this:

 private void sendCompletionEmailWithImage(DonationDTO donation) {
     try {
         // Check if donor has email
         if (donation.getDonor() == null || donation.getDonor().getEmail() == null || 
             donation.getDonor().getEmail().trim().isEmpty()) {
             System.out.println("⚠️ Cannot send email - donor email not available");
             return;
         }

         String donorName = donation.getDonor().getName() != null ? 
                          donation.getDonor().getName() : "Valued Donor";
         String donorEmail = donation.getDonor().getEmail();
         String ngoName = donation.getNgo() != null ? 
                        donation.getNgo().getNgoName() : "Our Organization";

         // Format the donation details
         String donationItem = getDonationItemDescription(donation);
         String quantity = donation.getQuantity() != null ? donation.getQuantity() : "N/A";
         
         // Format completed date
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
         String completedDate = donation.getCompletedAt() != null ? 
                              donation.getCompletedAt().format(formatter) : "Recently";

         // Build email content
         String subject = "🎉 Your Donation Has Been Completed!";
         
         String attachmentNote = donation.getProofImageBase64() != null && !donation.getProofImageBase64().isEmpty() 
             ? "📸 Please find attached proof image of the donation impact.\n\n" 
             : "";
         
         String message = String.format(
             "Dear %s,\n\n" +
             "We are delighted to inform you that your generous donation has been successfully completed!\n\n" +
             "═══════════════════════════════════════════════════════\n" +
             "                    DONATION DETAILS\n" +
             "═══════════════════════════════════════════════════════\n\n" +
             "✅ Status: COMPLETED\n" +
             "📦 Donation Type: %s\n" +
             "📝 Item: %s\n" +
             "📊 Quantity: %s\n" +
             "🏢 NGO: %s\n" +
             "📅 Completed On: %s\n\n" +
             "%s\n" +
             "%s\n" +
             "═══════════════════════════════════════════════════════\n\n" +
             "Your contribution has made a real difference in the lives of those who need it most. " +
             "Thank you for your compassion and generosity!\n\n" +
             "%s\n\n" +
             "If you have any questions or would like to know more about the impact of your donation, " +
             "please feel free to reach out to us.\n\n" +
             "With heartfelt gratitude,\n" +
             "The Food Donation Team\n\n" +
             "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
             "This is an automated notification. Please do not reply to this email.\n" +
             "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
             
             donorName,
             donation.getDonationType(),
             donationItem,
             quantity,
             ngoName,
             completedDate,
             getImpactMessage(donation),
             attachmentNote,
             getThankYouMessage(donation.getDonationType())
         );

         // Send email with or without attachment
         if (donation.getProofImageBase64() != null && !donation.getProofImageBase64().isEmpty()) {
             emailService.sendEmailWithAttachment(
                 donorEmail, 
                 subject, 
                 message,
                 donation.getProofImageBase64(),
                 donation.getProofImageName(),
                 donation.getProofImageType()
             );
         } else {
             emailService.sendEmail(donorEmail, subject, message);
         }
         
         System.out.println("✅ Completion email sent to: " + donorEmail);
         
     } catch (Exception e) {
         System.err.println("❌ Failed to send completion email: " + e.getMessage());
         e.printStackTrace();
     }
 }

 // Keep the existing helper methods:
 // - getDonationItemDescription()
 // - getImpactMessage()
 // - getThankYouMessage()

    // Helper method to get donation item description
    private String getDonationItemDescription(DonationDTO donation) {
        if (donation.getFoodName() != null && !donation.getFoodName().isEmpty()) {
            return donation.getFoodName() + 
                   (donation.getMealType() != null ? " (" + donation.getMealType() + ")" : "");
        } else if (donation.getClothesType() != null && !donation.getClothesType().isEmpty()) {
            return donation.getClothesType();
        } else if (donation.getItemName() != null && !donation.getItemName().isEmpty()) {
            return donation.getItemName();
        } else if ("MONEY".equals(donation.getDonationType()) && donation.getAmount() != null) {
            return "₹" + donation.getAmount();
        }
        return "Various Items";
    }

    // Helper method to get impact message
    private String getImpactMessage(DonationDTO donation) {
        StringBuilder impact = new StringBuilder();
        
        if (donation.getBeneficiariesCount() != null && donation.getBeneficiariesCount() > 0) {
            impact.append(String.format("💚 Your donation helped %d %s!\n", 
                donation.getBeneficiariesCount(),
                donation.getBeneficiariesCount() == 1 ? "person" : "people"));
        }
        
        if (donation.getImpactDescription() != null && !donation.getImpactDescription().trim().isEmpty()) {
            impact.append("📖 Impact Story: ").append(donation.getImpactDescription()).append("\n");
        }
        
        if (impact.length() == 0) {
            impact.append("💚 Your donation has reached those in need and made a positive impact!\n");
        }
        
        return impact.toString();
    }

    // Helper method to get thank you message based on donation type
    private String getThankYouMessage(String donationType) {
        switch (donationType) {
            case "FOOD":
                return "Your food donation has helped feed hungry families and individuals. " +
                       "Every meal matters, and your contribution is deeply appreciated.";
            
            case "CLOTHES":
                return "Your clothing donation has provided warmth and dignity to those in need. " +
                       "Thank you for helping us clothe the community.";
            
            case "ESSENTIALS":
                return "Your donation of essential items has made daily life easier for those facing hardship. " +
                       "These necessities make a real difference.";
            
            case "MONEY":
                return "Your financial contribution enables us to address the most urgent needs " +
                       "and support our ongoing programs. Thank you for your trust and generosity.";
            
            default:
                return "Your generous donation is making a meaningful difference in our community. " +
                       "Thank you for your support!";
        }
    }

    // Schedule pickup (specific endpoint for NGO)
    @PutMapping("/schedule-pickup/{donationId}")
    public ResponseEntity<?> schedulePickup(
            @PathVariable int donationId,
            @RequestBody Map<String, Object> payload
    ) {
        try {
            DonationDTO donation = donationRepository.findById(donationId)
                    .orElseThrow(() -> new RuntimeException("Donation not found"));

            String pickupDateStr = (String) payload.get("pickupDate");
            String pickupAddress = (String) payload.get("pickupAddress");
            String instructions = (String) payload.get("specialInstructions");
            String ngoNotes = (String) payload.get("ngoNotes");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime pickupDate = LocalDateTime.parse(pickupDateStr);

            donation.setStatus(DonationStatus.SCHEDULED);
            donation.setScheduledAt(now);
            donation.setPickupScheduledDate(pickupDate);
            
            if (pickupAddress != null) {
                donation.setPickupAddress(pickupAddress);
            }
            if (instructions != null) {
                donation.setSpecialInstructions(instructions);
            }
            if (ngoNotes != null) {
                donation.setNgoNotes(ngoNotes);
            }

            donation.setStatusMessage("Pickup scheduled for " + pickupDate.toString());
            donation.setUpdatedBy("NGO");

            donationRepository.save(donation);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("donation", donation);
            response.put("message", "Pickup scheduled successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get donation statistics for donor
    @GetMapping("/donor/{donorId}/stats")
    public ResponseEntity<?> getDonorStats(@PathVariable int donorId) {
        try {
            List<DonationDTO> donations = donationRepository.findByDonorId(donorId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDonations", donations.size());
            stats.put("pending", donations.stream().filter(d -> d.getStatus() == DonationStatus.PENDING).count());
            stats.put("confirmed", donations.stream().filter(d -> d.getStatus() == DonationStatus.CONFIRMED).count());
            stats.put("scheduled", donations.stream().filter(d -> d.getStatus() == DonationStatus.SCHEDULED).count());
            stats.put("pickedUp", donations.stream().filter(d -> d.getStatus() == DonationStatus.PICKED_UP).count());
            stats.put("inTransit", donations.stream().filter(d -> d.getStatus() == DonationStatus.IN_TRANSIT).count());
            stats.put("delivered", donations.stream().filter(d -> d.getStatus() == DonationStatus.DELIVERED).count());
            stats.put("completed", donations.stream().filter(d -> d.getStatus() == DonationStatus.COMPLETED).count());

            int totalBeneficiaries = donations.stream()
                    .mapToInt(d -> d.getBeneficiariesCount() != null ? d.getBeneficiariesCount() : 0)
                    .sum();
            stats.put("totalBeneficiaries", totalBeneficiaries);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}