package medipredict.model;

/** Master medicine record (not batch-specific). */
public class Medicine {
    private final String medicineId;
    private String name;
    private String category;
    private boolean essential;
    private double unitPrice;
    private int minStockLevel;
    private int reorderLevel;
    /** Approximate seasonal demand multiplier by month (1-12), default 1.0. */
    private final double[] seasonalFactors = new double[13];

    public Medicine(String medicineId, String name, String category, boolean essential,
                     double unitPrice, int minStockLevel, int reorderLevel) {
        this.medicineId = medicineId;
        this.name = name;
        this.category = category;
        this.essential = essential;
        this.unitPrice = unitPrice;
        this.minStockLevel = minStockLevel;
        this.reorderLevel = reorderLevel;
        for (int i = 1; i <= 12; i++) seasonalFactors[i] = 1.0;
    }

    public void setSeasonalFactor(int month, double factor) {
        if (month < 1 || month > 12) throw new IllegalArgumentException("Month must be 1-12");
        seasonalFactors[month] = factor;
    }

    public double getSeasonalFactor(int month) {
        if (month < 1 || month > 12) return 1.0;
        return seasonalFactors[month];
    }

    public String getMedicineId() { return medicineId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isEssential() { return essential; }
    public void setEssential(boolean essential) { this.essential = essential; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }
    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    @Override
    public String toString() {
        return String.format("%-6s %-20s %-12s %-9s Rs.%-8.2f min:%-5d reorder:%-5d",
                medicineId, name, category, essential ? "ESSENTIAL" : "normal", unitPrice, minStockLevel, reorderLevel);
    }
}
