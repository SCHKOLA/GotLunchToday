package de.schkola.kitchenscanner.database;

import android.content.Context;
import androidx.room.Room;

public class DatabaseAccess {

    private final String DB_NAME = "db_task";
    private final LunchDatabase database;

    public DatabaseAccess(Context context) {
        database = Room.databaseBuilder(context, LunchDatabase.class, DB_NAME).build();
    }

    public LunchDatabase getDatabase() {
        return database;
    }
}
