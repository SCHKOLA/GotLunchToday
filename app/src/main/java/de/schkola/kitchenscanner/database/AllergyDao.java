package de.schkola.kitchenscanner.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface AllergyDao {

    @Insert
    void insertAllergy(Allergy allergy);

    @Query("SELECT * FROM allergies")
    List<Allergy> getAll();

    @Query("SELECT * FROM allergies WHERE xba = :xba")
    List<Allergy> getAllergies(int xba);
}
