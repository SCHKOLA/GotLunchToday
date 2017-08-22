/*
 * MIT License
 *
 * Copyright 2016 Niklas Merkelt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.schkola.kitchenscanner.util;

import android.app.Activity;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Contains the data of a Person
 */
public class Person {

    private static final SparseArray<Person> all = new SparseArray<>();
    private final String person_name;
    private final String clazz;
    private final int lunch;
    private final File f;
    private String allergies = "";
    private int gotLunch = -1;

    public Person(int xba, String clazz, String name, int lunch, Activity activity) {
        this.clazz = clazz;
        this.person_name = name;
        this.lunch = lunch;
        all.put(xba, this);
        f = new File(activity.getDir("Lunch", Activity.MODE_PRIVATE), xba + ".txt");
    }

    public static Person getByXBA(int xba) {
        return all.get(xba);
    }

    public static SparseArray<Person> getPersons() {
        return all;
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

    public int getRawLunch() {
        return lunch;
    }

    public String getPersonName() {
        return person_name;
    }

    public int getGotLunch() {
        if (gotLunch == -1) {
            try {
                BufferedReader buffer = new BufferedReader(new FileReader(getLunchFile()));
                String lunch = buffer.readLine();
                buffer.close();
                gotLunch = Integer.parseInt(lunch);
            } catch (Exception e) {
                gotLunch = 0;
            }
        }
        return gotLunch;
    }

    public String getAllergies() {
        return allergies;
    }

    public void addAllergy(String s) {
        if (allergies.equals("")) {
            allergies = s;
        } else {
            allergies += ", " + s;
        }
    }

    public void gotLunch() {
        gotLunch = getGotLunch() + 1;
        getLunchFile().delete();
        try {
            FileOutputStream fos = new FileOutputStream(getLunchFile());
            PrintWriter pw = new PrintWriter(fos);
            pw.println(gotLunch);
            pw.flush();
            pw.close();
            fos.close();
        } catch (IOException ignored) {
        }
    }
}
