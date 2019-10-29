package de.schkola.kitchenscanner.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customers")
public class Customer {

    @PrimaryKey
    public int xba;
    public String name;
    public String grade;
    public byte lunch;
    public byte gotLunch;

}
