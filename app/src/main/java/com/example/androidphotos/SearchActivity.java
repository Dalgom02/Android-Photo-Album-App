package com.example.androidphotos;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.AutoCompleteTextView;


public class SearchActivity extends AppCompatActivity {

    private AutoCompleteTextView searchAutoCompleteTextView;
    private SearchAutoCompleteAdapter searchAutoCompleteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        searchAutoCompleteTextView = findViewById(R.id.searchAutoCompleteTextView);
        searchAutoCompleteAdapter = new SearchAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoCompleteTextView.setAdapter(searchAutoCompleteAdapter);
    }

}
