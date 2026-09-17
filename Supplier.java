package medipredict.model;

/** Medicine supplier with an average lead time used for reorder-point calculations. */
public class Supplier {
    private final String supplierId;
    private String name;
    private int leadTimeDays;

    public Supplier(String supplierId, String name, int leadTimeDays) {
        this.supplierId = supplierId;
        this.name = name;
        this.leadTimeDays = leadTimeDays;
    }

    public String getSupplierId() { return supplierId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(int leadTimeDays) { this.leadTimeDays = leadTimeDays; }
}
