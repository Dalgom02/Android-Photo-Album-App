package com.example.androidphotos;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SearchAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> items;
    private List<String> suggestions;

    public SearchAutoCompleteAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        items = new ArrayList<>();
        suggestions = new ArrayList<>();
    }

    // Override the getCount and getItem methods to return data from the suggestions list.
    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    // Create a custom filter to handle the filtering logic.
    @Override
    public Filter getFilter() {
        return new SearchFilter(this, items);
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}
