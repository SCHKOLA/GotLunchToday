package de.schkola.kitchenscanner.util;

import androidx.annotation.NonNull;
import de.schkola.kitchenscanner.database.Allergy;
import java.util.List;
import org.jetbrains.annotations.Contract;

public interface StringUtil {

    @Contract(pure = true)
    static byte getLunch(@NonNull String lunch) {
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

    @NonNull
    @Contract(pure = true)
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

    @NonNull
    static String getAllergies(@NonNull List<Allergy> allergies) {
        StringBuilder s = new StringBuilder();
        for (Allergy a : allergies) {
            s.append(a.allergy).append(", ");
        }
        return s.toString();
    }
}
