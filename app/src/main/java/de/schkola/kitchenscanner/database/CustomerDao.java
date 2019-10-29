package de.schkola.kitchenscanner.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface CustomerDao {

    @Insert
    void insertCustomer(Customer customer);

    @Query("SELECT * FROM customers")
    List<Customer> getAll();

    @Query("SELECT * FROM customers WHERE xba = :xba")
    Customer getCustomer(int xba);

    @Query("DELETE FROM customers")
    void deleteAll();
}
