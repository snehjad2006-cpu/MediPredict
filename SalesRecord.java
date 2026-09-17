package medipredict.model;

import java.time.LocalDate;

/** A single recorded sale/dispense event. */
public class SalesRecord {
    private final String saleId;
    private final String medicineId;
    private final int quantitySold;
    private final LocalDate date;
    private final String pharmacyId;

    public SalesRecord(String saleId, String medicineId, int quantitySold, LocalDate date, String pharmacyId) {
        if (quantitySold <= 0) throw new IllegalArgumentException("Quantity sold must be positive");
        this.saleId = saleId;
        this.medicineId = medicineId;
        this.quantitySold = quantitySold;
        this.date = date;
        this.pharmacyId = pharmacyId;
    }

    public String getSaleId() { return saleId; }
    public String getMedicineId() { return medicineId; }
    public int getQuantitySold() { return quantitySold; }
    public LocalDate getDate() { return date; }
    public String getPharmacyId() { return pharmacyId; }
}
