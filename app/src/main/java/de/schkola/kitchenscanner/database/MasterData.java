package de.schkola.kitchenscanner.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "master_data")
public class MasterData {

    @PrimaryKey
    @NonNull
    public String key;
    @NonNull
    public String value;
}
