package de.schkola.kitchenscanner.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Customer.class}, version = 1)
public abstract class LunchDatabase extends RoomDatabase {
    public abstract CustomerDao customerDao();
    public abstract AllergyDao allergyDao();
    public abstract LunchDao lunchDao();
}
