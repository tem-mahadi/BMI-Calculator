package com.temmahadi.bmicalculator.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BmiEntryDao {

    @Insert
    void insert(BmiEntry entry);

    @Query("SELECT * FROM bmi_entries ORDER BY timestamp DESC")
    List<BmiEntry> getAllEntries();

    @Query("SELECT * FROM bmi_entries ORDER BY timestamp DESC LIMIT 1")
    BmiEntry getLatestEntry();

    @Query("SELECT * FROM bmi_entries ORDER BY timestamp ASC LIMIT 1")
    BmiEntry getFirstEntry();

    @Query("SELECT AVG(bmi) FROM bmi_entries")
    Double getAverageBmi();

    @Query("SELECT COUNT(*) FROM bmi_entries")
    int getCount();
}
