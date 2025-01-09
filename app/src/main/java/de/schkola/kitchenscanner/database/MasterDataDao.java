package de.schkola.kitchenscanner.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MasterDataDao {

    @Insert
    void insertData(MasterData masterData);

    @Update
    void updateData(MasterData masterData);

    @Query("SELECT value FROM master_data WHERE `key` = :key")
    String getValue(String key);

    @Query("DELETE FROM master_data WHERE `key` = :key")
    void deleteKey(String key);
}
