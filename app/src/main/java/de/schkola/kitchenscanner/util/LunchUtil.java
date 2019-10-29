package de.schkola.kitchenscanner.util;

public class LunchUtil {

    public static byte getLunch(String lunch) {
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

    public static String getLunch(int lunch) {
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
}
