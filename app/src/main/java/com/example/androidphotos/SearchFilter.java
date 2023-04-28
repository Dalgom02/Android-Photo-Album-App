package com.example.androidphotos;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class SearchFilter extends Filter {
    private SearchAutoCompleteAdapter adapter;
    private List<String> items;

    public SearchFilter(SearchAutoCompleteAdapter adapter, List<String> items) {
        this.adapter = adapter;
        this.items = items;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        List<String> suggestions = new ArrayList<>();

        if (constraint != null && !constraint.toString().trim().isEmpty()) {
            for (String item : items) {
                if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                    suggestions.add(item);
                }
            }
        }

        filterResults.values = suggestions;
        filterResults.count = suggestions.size();

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.clear();
        adapter.clear();
        if (results != null && results.count > 0) {
            adapter.addAll((List<String>) results.values);
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetInvalidated();
        }
    }
}
