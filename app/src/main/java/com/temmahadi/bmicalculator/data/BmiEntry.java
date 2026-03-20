package com.temmahadi.bmicalculator.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bmi_entries")
public class BmiEntry {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public long timestamp;
    public double weight;
    public double heightMain;
    public double heightInch;
    public double bmi;
    public String unitType;
    public String category;

    public BmiEntry(long timestamp, double weight, double heightMain, double heightInch, double bmi, String unitType, String category) {
        this.timestamp = timestamp;
        this.weight = weight;
        this.heightMain = heightMain;
        this.heightInch = heightInch;
        this.bmi = bmi;
        this.unitType = unitType;
        this.category = category;
    }
}
