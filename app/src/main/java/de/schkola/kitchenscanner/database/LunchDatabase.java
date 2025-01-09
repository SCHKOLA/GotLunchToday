package de.schkola.kitchenscanner.database;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Customer.class, Allergy.class, MasterData.class}, version = 2, autoMigrations = {@AutoMigration(from = 1, to = 2)})
public abstract class LunchDatabase extends RoomDatabase {
    public abstract CustomerDao customerDao();
    public abstract AllergyDao allergyDao();
    public abstract LunchDao lunchDao();

    public abstract MasterDataDao masterDataDao();
}
