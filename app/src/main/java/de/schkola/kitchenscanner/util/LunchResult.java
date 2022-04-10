package de.schkola.kitchenscanner.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.schkola.kitchenscanner.database.Allergy;
import de.schkola.kitchenscanner.database.Customer;
import java.util.List;

public class LunchResult {

    private final @Nullable
    Customer customer;
    private final @NonNull
    List<Allergy> allergies;

    public LunchResult(@Nullable Customer customer, @NonNull List<Allergy> allergies) {
        this.customer = customer;
        this.allergies = allergies;
    }

    @Nullable
    public Customer getCustomer() {
        return customer;
    }

    @NonNull
    public List<Allergy> getAllergies() {
        return allergies;
    }
}
