package com.example.androidphotos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.TextUtils;
import android.widget.TextView;


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

    private TextView tagInfoTextView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_view);

        searchAutoCompleteTextView = findViewById(R.id.searchAutoCompleteTextView);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        tagInfoTextView = findViewById(R.id.tagInfoTextView);

        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        photoAdapter = new PhotoAdapter(this, new ArrayList<>());
        searchResultsRecyclerView.setAdapter(photoAdapter);

        photoAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position >= 0) { // Check if a valid item is clicked
                    Photo selectedPhoto = photoAdapter.getPhotos().get(position);
                    String personTags = TextUtils.join(", ", selectedPhoto.getPersonTags());
                    String locationTags = TextUtils.join(", ", selectedPhoto.getLocationTags());
                    tagInfoTextView.setText("Person Tags: " + personTags + "\nLocation Tags: " + locationTags);
                } else {
                    tagInfoTextView.setText("Person Tags: \nLocation Tags: ");
                }
            }
        });

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
        if (query.trim().isEmpty()) {
            photoAdapter.setPhotos(new ArrayList<>());
            photoAdapter.notifyDataSetChanged();
            tagInfoTextView.setText("Person Tags: \nLocation Tags: "); // Clear the tag information
            return;
        }

        query = query.toLowerCase(); // Convert the query to lowercase
        List<Album> albums = DataManager.loadAlbums(this);
        List<Photo> matchingPhotos = new ArrayList<>();

        // Updated regex pattern to match both uppercase and lowercase "and" and "or" operators without quotes
        Pattern pattern = Pattern.compile("(person|location)=([\\w\\s]+)|\\b(?i)(and|or)\\b");
        Matcher matcher = pattern.matcher(query);

        List<String[]> matchList = new ArrayList<>();
        String searchType = "";

        while (matcher.find()) {
            if (matcher.group(1) != null && matcher.group(2) != null) {
                String tagType = matcher.group(1);
                String tagValue = matcher.group(2).trim();
                matchList.add(new String[]{tagType, tagValue});
            } else if (matcher.group(3) != null) {
                searchType = matcher.group(3).toUpperCase();
            }
        }

        if (matchList.isEmpty()) {
            return;
        }

        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                boolean firstTagMatch = hasTagStartsWith(photo, matchList.get(0)[0], matchList.get(0)[1]);
                boolean secondTagMatch = false;

                if (matchList.size() > 1) {
                    secondTagMatch = hasTagStartsWith(photo, matchList.get(1)[0], matchList.get(1)[1]);
                }

                if (!searchType.isEmpty()) {
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



    private boolean hasTagStartsWith(Photo photo, String tagType, String tagValue) {
        List<String> tags;
        if (tagType.equals("person")) {
            tags = photo.getPersonTags();
        } else if (tagType.equals("location")) {
            tags = photo.getLocationTags();
        } else {
            return false;
        }

        for (String tag : tags) {
            if (tag.toLowerCase().startsWith(tagValue.toLowerCase())) {
                return true;
            }
        }

        return false;
    }






}
