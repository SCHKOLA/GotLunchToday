package de.schkola.kitchenscanner.database;

import androidx.room.Dao;
import androidx.room.Query;
import java.util.List;

@Dao
public interface LunchDao {

    @Query("SELECT COUNT(*) FROM customers WHERE lunch = :lunch")
    int getLunchCount(int lunch);

    @Query("SELECT COUNT(*) FROM customers WHERE lunch = :lunch AND gotLunch > 0")
    int getDispensedLunchCount(int lunch);

    @Query("SELECT COUNT(*) FROM customers WHERE lunch = :lunch AND gotLunch = 0")
    int getToDispenseLunchCount(int lunch);

    @Query("SELECT * FROM customers WHERE lunch != 0 AND gotLunch = 0")
    List<Customer> getToDispenseLunch();

    @Query("SELECT * FROM customers WHERE lunch = :lunch AND gotLunch = 0")
    List<Customer> getToDispenseLunch(int lunch);
}
