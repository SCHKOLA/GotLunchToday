package de.schkola.kitchenscanner.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "allergies")
public class Allergy {

    @PrimaryKey(autoGenerate = true)
    public int allergyId;
    public String allergy;
    public int xba;

}
