package medipredict.model;

/** Represents a Jan Aushadhi store / pharmacy / health center. */
public class Pharmacy {
    private final String pharmacyId;
    private String name;
    private String location;

    public Pharmacy(String pharmacyId, String name, String location) {
        this.pharmacyId = pharmacyId;
        this.name = name;
        this.location = location;
    }

    public String getPharmacyId() { return pharmacyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return String.format("%-6s %-25s %s", pharmacyId, name, location);
    }
}
