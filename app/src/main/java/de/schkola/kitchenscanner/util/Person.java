package de.schkola.kitchenscanner.util;

import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import de.schkola.kitchenscanner.activity.MainActivity;

/**
 * Diese Klasse repräsentiert eine Person in der deren Daten gespeichert sind.
 */
public class Person {

    private static SparseArray<Person> all = new SparseArray<>();
    private final String person_name;
    private final String clazz;
    private final int lunch;
    private final File f;
    private String allergie = "";

    public Person(int xba, String clazz, String name, int lunch) {
        this.clazz = clazz;
        this.person_name = name;
        this.lunch = lunch;
        all.put(xba, this);
        f = new File(MainActivity.getInstance().getDir("Lunch", MainActivity.MODE_PRIVATE), xba + ".txt");
    }

    public static Person getByXBA(int xba) {
        return all.get(xba);
    }

    private File getLunchFile() {
        return f;
    }

    public String getClazz() {
        return clazz.equals("Mitarbeiter") ? clazz : "Klasse " + clazz;
    }

    public String getLunch() {
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

    public String getPersonName() {
        return person_name;
    }

    public int getGotLunch() {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(getLunchFile()));
            String lunch = buffer.readLine();
            buffer.close();
            return Integer.parseInt(lunch);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getAllergie() {
        return allergie;
    }

    public void addAllergie(String s) {
        if (allergie.equals("")) {
            allergie = s;
        } else {
            allergie += ", " + s;
        }
    }

    public void gotLunch() {
        int lunch = getGotLunch();
        getLunchFile().delete();
        try {
            FileOutputStream fos = new FileOutputStream(getLunchFile());
            PrintWriter pw = new PrintWriter(fos);
            pw.println(lunch + 1);
            pw.flush();
            pw.close();
            fos.close();
        } catch (IOException ignored) {
        }
    }
}
