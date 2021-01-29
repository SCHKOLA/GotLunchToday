package de.schkola.kitchenscanner.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CustomerDao {

    @Insert
    void insertCustomer(Customer customer);

    @Update
    void updateCustomer(Customer customer);

    @Query("SELECT * FROM customers")
    List<Customer> getAll();

    @Query("SELECT * FROM customers WHERE xba = :xba")
    Customer getCustomer(int xba);

    @Query("SELECT * FROM customers WHERE gotLunch > 0")
    List<Customer> getCustomerGotLunch();

    @Query("DELETE FROM customers")
    void deleteAll();
}
