package de.schkola.kitchenscanner.database;

import android.content.Context;
import androidx.room.Room;

public class DatabaseAccess {

    private final LunchDatabase database;

    public DatabaseAccess(Context context) {
        database = Room.databaseBuilder(context, LunchDatabase.class, "db_lunch").build();
    }

    public LunchDatabase getDatabase() {
        return database;
    }
}
