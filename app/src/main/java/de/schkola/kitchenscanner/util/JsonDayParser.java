/*
 * MIT License
 *
 * Copyright 2017 Niklas Merkelt
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
import android.util.JsonReader;

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
