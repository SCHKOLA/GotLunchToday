package de.schkola.kitchenscanner.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Für CSV Datei nach Schema
 * [Wochentag],[Datum],[Klassenstufe],[XBA-Nummer],"[Nachname],[Vorname]",[Essen]
 */
public class CSVFile_Teilnahme {
    InputStream inputStream;

    public CSVFile_Teilnahme(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List<String[]> read(boolean b) throws UnsupportedEncodingException {
        List<String[]> resultList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1252"));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] split1 = csvLine.split("\"");
                String[] row = csvLine.split(",");
                row[4] = split1[1];
                resultList.add(row);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        return resultList;
    }
}
