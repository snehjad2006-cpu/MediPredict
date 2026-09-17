package medipredict.model;

/** Output of ReorderEngine: a suggested purchase order awaiting staff approval. */
public class ReorderRecommendation {
    public enum Status { PENDING, APPROVED, REJECTED }

    private final String medicineId;
    private final int currentStock;
    private final double predictedDemand;
    private final int recommendedOrderQty;
    private final RiskLevel priority;
    private Status status = Status.PENDING;

    public ReorderRecommendation(String medicineId, int currentStock, double predictedDemand,
                                  int recommendedOrderQty, RiskLevel priority) {
        this.medicineId = medicineId;
        this.currentStock = currentStock;
        this.predictedDemand = predictedDemand;
        this.recommendedOrderQty = recommendedOrderQty;
        this.priority = priority;
    }

    public String getMedicineId() { return medicineId; }
    public int getCurrentStock() { return currentStock; }
    public double getPredictedDemand() { return predictedDemand; }
    public int getRecommendedOrderQty() { return recommendedOrderQty; }
    public RiskLevel getPriority() { return priority; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("%-6s current:%-5d predictedDemand:%-8.1f recommendedOrder:%-6d priority:%-8s status:%s",
                medicineId, currentStock, predictedDemand, recommendedOrderQty, priority, status);
    }
}
