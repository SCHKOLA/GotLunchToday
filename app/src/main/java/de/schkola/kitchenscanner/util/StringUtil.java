package de.schkola.kitchenscanner.util;

import de.schkola.kitchenscanner.database.Allergy;
import java.util.List;

public interface StringUtil {

    static byte getLunch(String lunch) {
        switch (lunch) {
            case "1":
                return 1;
            case "2":
                return 2;
            case "3":
                return 3;
            default:
                return 0;
        }
    }

    static String getLunch(int lunch) {
        switch (lunch) {
            case 1:
                return "A";
            case 2:
                return "B";
            case 3:
                return "S";
            default:
                return "X";
        }
    }

    static String getAllergies(List<Allergy> allergies) {
        StringBuilder s = new StringBuilder();
        for (Allergy a : allergies) {
            s.append(a.allergy).append(", ");
        }
        return s.toString();
    }
}
