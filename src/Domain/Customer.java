
package Domain;

public class Customer {
    private int customerId;           // AUTO_INCREMENT
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;             // nullable
    private boolean active;           // maps to 'Y'/'N'
    private double discountRate;      // DECIMAL(5,2)
    private String note;              // nullable

    // Minimal constructor for lists( NOT NULL Only )
    public Customer(int customerId, String fullName) {
        this.customerId = customerId;
        String[] parts = fullName.split("\\s+", 2);
        this.firstName = parts.length > 0 ? parts[0] : "";
        this.lastName  = parts.length > 1 ? parts[1] : "";
        this.active = true;
    }

    // Full constructor
    public Customer(int customerId, String firstName, String lastName,
                    String phoneNumber, String email, boolean active,
                    double discountRate, String note) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.active = active;
        this.discountRate = discountRate;
        this.note = note;
    }

    // Getters/Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double getDiscountRate() { return discountRate; }
    public void setDiscountRate(double discountRate) { this.discountRate = discountRate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getFullName() { return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName); }

    @Override
    public String toString() {
        return customerId + " - " + getFullName().trim();
    }
}
