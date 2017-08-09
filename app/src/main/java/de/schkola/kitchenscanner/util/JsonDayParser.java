package de.schkola.kitchenscanner.util;

import android.app.Activity;
import android.util.JsonReader;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonDayParser {

    private final File f;
    private final Activity activity;

    public JsonDayParser(File f, Activity activity) {
        this.f = f;
        this.activity = activity;
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
        String clazz = null;
        String name = null;
        int lunch = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String id = reader.nextName();
            switch (id) {
                case "xba":
                    xba = reader.nextInt();
                    break;
                case "class":
                    clazz = reader.nextString();
                    break;
                case "name":
                    name = reader.nextString();
                    break;
                case "lunch":
                    try {
                        lunch = reader.nextInt();
                    } catch (NumberFormatException e) {
                        reader.skipValue();
                        lunch = 0;
                    }
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        if (clazz != null && name != null) {
            new Person(xba, clazz, name, lunch, activity);
        }
    }

}
