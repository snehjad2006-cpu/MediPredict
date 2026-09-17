package medipredict.model;

import java.time.LocalDateTime;

/** A system-generated alert (stockout, expiry, anomaly, etc.) for staff review. */
public class Alert {
    public enum Type { STOCKOUT, EXPIRY, ANOMALY, TRANSFER_SUGGESTION, PRIORITY }

    private final String alertId;
    private final Type type;
    private final RiskLevel severity;
    private final String medicineId;
    private final String message;
    private final LocalDateTime timestamp;

    public Alert(String alertId, Type type, RiskLevel severity, String medicineId, String message) {
        this.alertId = alertId;
        this.type = type;
        this.severity = severity;
        this.medicineId = medicineId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public String getAlertId() { return alertId; }
    public Type getType() { return type; }
    public RiskLevel getSeverity() { return severity; }
    public String getMedicineId() { return medicineId; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }

    private String icon() {
        switch (severity) {
            case CRITICAL: return "\uD83D\uDD34"; // red circle
            case HIGH: return "\uD83D\uDFE0"; // orange circle
            case MEDIUM: return "\uD83D\uDFE1"; // yellow circle
            default: return "\uD83D\uDFE2"; // green circle
        }
    }

    @Override
    public String toString() {
        return String.format("%s [%s/%s] %s - %s", icon(), type, severity, medicineId, message);
    }
}
