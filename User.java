package medipredict.model;

/** System user (Admin or Pharmacist/Staff). Password is stored as a SHA-256 hash. */
public class User {
    private final String userId;
    private final String username;
    private String passwordHash;
    private Role role;
    private String pharmacyId;

    public User(String userId, String username, String passwordHash, Role role, String pharmacyId) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.pharmacyId = pharmacyId;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(String pharmacyId) { this.pharmacyId = pharmacyId; }
}
