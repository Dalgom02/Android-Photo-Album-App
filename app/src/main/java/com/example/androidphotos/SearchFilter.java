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
            String constraintText = constraint.toString().toLowerCase().trim();
            String[] constraintParts = constraintText.split("\\s+");

            for (String item : items) {
                String[] itemParts = item.toLowerCase().split("\\s+");
                boolean match = true;
                for (int i = 0; i < constraintParts.length; i++) {
                    if (i < itemParts.length && !itemParts[i].startsWith(constraintParts[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
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
        // Remove the duplicated 'adapter.clear()' call.
        adapter.clear();
        if (results != null && results.count > 0) {
            adapter.addAll((List<String>) results.values);
            adapter.notifyDataSetChanged();
        } else {
            adapter.notifyDataSetInvalidated();
        }
    }

}
