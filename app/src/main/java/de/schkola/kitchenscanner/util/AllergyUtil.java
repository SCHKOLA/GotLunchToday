package de.schkola.kitchenscanner.util;

import de.schkola.kitchenscanner.database.Allergy;
import java.util.List;

public class AllergyUtil {

    public static String getAllergies(List<Allergy> allergies) {
        StringBuilder s = new StringBuilder();
        for (Allergy a : allergies) {
            s.append(a.allergy).append(", ");
        }
        return s.toString();
    }
}
