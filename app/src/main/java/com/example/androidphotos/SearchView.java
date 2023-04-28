package com.example.androidphotos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidphotos.PhotoAdapter;
import com.example.androidphotos.SearchAutoCompleteAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Album;
import model.DataManager;
import model.Photo;

public class SearchView extends AppCompatActivity {

    private AutoCompleteTextView searchAutoCompleteTextView;
    private RecyclerView searchResultsRecyclerView;
    private PhotoAdapter photoAdapter;
    private SearchAutoCompleteAdapter searchAutoCompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        searchAutoCompleteTextView = findViewById(R.id.searchAutoCompleteTextView);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        photoAdapter = new PhotoAdapter(this, new ArrayList<>());
        searchResultsRecyclerView.setAdapter(photoAdapter);

        searchAutoCompleteAdapter = new SearchAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoCompleteTextView.setAdapter(searchAutoCompleteAdapter);
        populateTags();

        searchAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void populateTags() {
        List<Album> albums = DataManager.loadAlbums(this);
        Set<String> uniqueTags = new HashSet<>();

        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                for (String personTag : photo.getPersonTags()) {
                    uniqueTags.add("person=" + personTag);
                }
                for (String locationTag : photo.getLocationTags()) {
                    uniqueTags.add("location=" + locationTag);
                }
            }
        }

        searchAutoCompleteAdapter.setItems(new ArrayList<>(uniqueTags));
    }

    private void performSearch(String query) {
        query = query.toLowerCase(); // Convert the query to lowercase
        List<Album> albums = DataManager.loadAlbums(this);
        List<Photo> matchingPhotos = new ArrayList<>();

        String[] queryParts = query.split(" ");
        String searchType = "";
        if (queryParts.length > 1) {
            searchType = queryParts[1].toUpperCase(); // "AND" or "OR"
        }

        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                boolean firstTagMatch = photo.hasTag(queryParts[0]);

                if (queryParts.length > 2) {
                    boolean secondTagMatch = photo.hasTag(queryParts[2]);

                    if (searchType.equals("AND") && firstTagMatch && secondTagMatch) {
                        matchingPhotos.add(photo);
                    } else if (searchType.equals("OR") && (firstTagMatch || secondTagMatch)) {
                        matchingPhotos.add(photo);
                    }
                } else if (firstTagMatch) {
                    matchingPhotos.add(photo);
                }
            }
        }

        photoAdapter.setPhotos(matchingPhotos);
        photoAdapter.notifyDataSetChanged();
    }

}
