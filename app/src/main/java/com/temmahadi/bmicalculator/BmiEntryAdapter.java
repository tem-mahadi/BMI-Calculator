package com.temmahadi.bmicalculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.temmahadi.bmicalculator.data.BmiEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BmiEntryAdapter extends RecyclerView.Adapter<BmiEntryAdapter.EntryViewHolder> {

    private final List<BmiEntry> entries;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

    public BmiEntryAdapter(List<BmiEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bmi_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        BmiEntry entry = entries.get(position);
        holder.tvDate.setText(dateFormat.format(new Date(entry.timestamp)));
        holder.tvBmi.setText(String.format(Locale.getDefault(), "BMI %.1f", entry.bmi));
        holder.tvCategory.setText(entry.category);

        if ("Metric".equals(entry.unitType)) {
            holder.tvDetails.setText(String.format(Locale.getDefault(), "%.1f kg, %.1f cm", entry.weight, entry.heightMain));
        } else {
            holder.tvDetails.setText(String.format(Locale.getDefault(), "%.1f lb, %.0f ft %.1f in", entry.weight, entry.heightMain, entry.heightInch));
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvBmi;
        TextView tvCategory;
        TextView tvDetails;

        EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvBmi = itemView.findViewById(R.id.tvBmi);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDetails = itemView.findViewById(R.id.tvDetails);
        }
    }
}
