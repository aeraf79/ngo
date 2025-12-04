package com.example.fooddonation.entity;

public enum DonationStatus {
    PENDING("Pending", "Donation added to cart, waiting for confirmation", "⏳"),
    CONFIRMED("Confirmed", "Donation confirmed, waiting for pickup scheduling", "✅"),
    SCHEDULED("Scheduled", "Pickup scheduled with donor", "📅"),
    PICKED_UP("Picked Up", "Donation collected from donor", "🚚"),
    IN_TRANSIT("In Transit", "Donation being transported to distribution center", "🛣️"),
    DELIVERED("Delivered", "Donation reached beneficiaries", "📦"),
    COMPLETED("Completed", "Donation process completed with acknowledgment", "🎉");

    private final String displayName;
    private final String description;
    private final String emoji;

    DonationStatus(String displayName, String description, String emoji) {
        this.displayName = displayName;
        this.description = description;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getStepNumber() {
        return this.ordinal();
    }

    public boolean isAfter(DonationStatus other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isBefore(DonationStatus other) {
        return this.ordinal() < other.ordinal();
    }
}