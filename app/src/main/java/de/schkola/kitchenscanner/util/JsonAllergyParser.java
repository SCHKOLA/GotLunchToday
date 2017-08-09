package de.schkola.kitchenscanner.util;

import android.util.JsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonAllergyParser {

    private final File f;

    public JsonAllergyParser(File f) {
        this.f = f;
    }

    public void parse() throws IOException {
        FileInputStream is = new FileInputStream(f.getAbsoluteFile());
        JsonReader reader = new JsonReader(new InputStreamReader(is, "ISO-8859-1"));
        try {
            readArray(reader);
        } finally {
            reader.close();
        }
    }

    private void readArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readPerson(reader);
        }
        reader.endArray();
    }

    private void readPerson(JsonReader reader) throws IOException {
        int xba = 0;
        String allergy = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String id = reader.nextName();
            switch (id) {
                case "xba":
                    xba = reader.nextInt();
                    break;
                case "allergy":
                    allergy = reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        if (allergy != null) {
            Person.getByXBA(xba).addAllergy(allergy);
        }
    }

}
