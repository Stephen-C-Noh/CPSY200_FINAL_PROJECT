
package Domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Rental {
    private int rentalId; // 0 until generated
    private LocalDate requestDate;
    private Customer customer;
    private List<RentalItem> items = new ArrayList<>();

    public Rental(LocalDate requestDate, Customer customer) {
        this.requestDate = requestDate;
        this.customer = customer;
    }

    public int getRentalId() { return rentalId; }
    public void setRentalId(int rentalId) { this.rentalId = rentalId; }

    public LocalDate getRequestDate() { return requestDate; }
    public Customer getCustomer() { return customer; }
    public List<RentalItem> getItems() { return items; }

    public void addItem(RentalItem item) { items.add(item); }

    // Inner class to keep project minimal
    public static class RentalItem {
        private Equipment equipment;
        private LocalDate rentalDate;
        private LocalDate returnDate;

        public RentalItem(Equipment equipment, LocalDate rentalDate, LocalDate returnDate) {
            this.equipment = equipment;
            this.rentalDate = rentalDate;
            this.returnDate = returnDate;
        }
        public Equipment getEquipment() { return equipment; }
        public LocalDate getRentalDate() { return rentalDate; }
        public LocalDate getReturnDate() { return returnDate; }
    }
}
