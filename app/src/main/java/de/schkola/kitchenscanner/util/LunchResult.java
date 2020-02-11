package de.schkola.kitchenscanner.util;

import de.schkola.kitchenscanner.database.Allergy;
import de.schkola.kitchenscanner.database.Customer;
import java.util.List;

public class LunchResult {

    private Customer customer;
    private List<Allergy> allergies;

    public LunchResult(Customer customer, List<Allergy> allergies) {
        this.customer = customer;
        this.allergies = allergies;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Allergy> getAllergies() {
        return allergies;
    }
}
