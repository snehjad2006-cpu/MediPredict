package medipredict.model;

import java.time.LocalDate;

/** A physical batch of stock for a medicine at a specific pharmacy. */
public class InventoryBatch {
    private final String batchId;
    private final String medicineId;
    private int quantity;
    private final LocalDate expiryDate;
    private final double purchasePrice;
    private final String supplierId;
    private final String pharmacyId;

    public InventoryBatch(String batchId, String medicineId, int quantity, LocalDate expiryDate,
                           double purchasePrice, String supplierId, String pharmacyId) {
        if (quantity < 0) throw new IllegalArgumentException("Batch quantity cannot be negative");
        this.batchId = batchId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.purchasePrice = purchasePrice;
        this.supplierId = supplierId;
        this.pharmacyId = pharmacyId;
    }

    public String getBatchId() { return batchId; }
    public String getMedicineId() { return medicineId; }
    public int getQuantity() { return quantity; }

    public void reduceQuantity(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive");
        if (amount > quantity) throw new IllegalStateException("Cannot reduce below zero stock in batch " + batchId);
        quantity -= amount;
    }

    public void increaseQuantity(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be positive");
        quantity += amount;
    }

    public LocalDate getExpiryDate() { return expiryDate; }
    public double getPurchasePrice() { return purchasePrice; }
    public String getSupplierId() { return supplierId; }
    public String getPharmacyId() { return pharmacyId; }

    public long daysUntilExpiry(LocalDate today) {
        return java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);
    }

    public boolean isExpired(LocalDate today) {
        return expiryDate.isBefore(today);
    }
}
