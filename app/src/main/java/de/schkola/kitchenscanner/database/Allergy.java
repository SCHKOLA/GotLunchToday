package de.schkola.kitchenscanner.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "allergies")
public class Allergy {

    @PrimaryKey
    public int allergyId;
    public String allergy;
    public int xba;

}
