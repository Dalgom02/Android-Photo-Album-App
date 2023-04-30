package com.example.androidphotos;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;



import java.util.List;

import model.Album;
import model.DataManager;

public class AlbumView extends AppCompatActivity {

    private List<Album> albums;
    private AlbumAdapter albumAdapter;
    private ListView albumListView;

    private int selectedAlbumPosition = -1;
    private View previousSelectedView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        // Load albums
        albums = DataManager.loadAlbums(this);

        // Set up the adapter
        albumAdapter = new AlbumAdapter(this, android.R.layout.simple_list_item_1, albums);
        albumListView = findViewById(R.id.albumsListView);
        albumListView.setAdapter(albumAdapter);
        albumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previousSelectedView != null) {
                    // Reset the background color of the previously selected view
                    previousSelectedView.setBackgroundColor(Color.TRANSPARENT);
                }
                // Set the background color of the selected view
                view.setBackgroundColor(Color.LTGRAY);
                previousSelectedView = view;
                selectedAlbumPosition = position;
            }
        });

        // Set up the add button
        Button addAlbumButton = findViewById(R.id.addAlbumButton);
        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum();
            }
        });

        // Set up the edit button
        Button editAlbumButton = findViewById(R.id.editAlbumButton);
        editAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAlbum();
            }
        });

        // Set up the open button
        Button openAlbumButton = findViewById(R.id.openAlbumButton);
        openAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
            }
        });


        // Set up the search button
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearch();
            }
        });

    }

    private void openSearch() {
        Intent intent = new Intent(AlbumView.this, SearchView.class);
        startActivity(intent);
    }


    private void addAlbum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Album");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String albumName = input.getText().toString().trim();

                if (albumName.isEmpty()) {
                    Toast.makeText(AlbumView.this, "Album name cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Album album : albums) {
                    if (album.getAlbumName().equalsIgnoreCase(albumName)) {
                        Toast.makeText(AlbumView.this, "Album with this name already exists.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Album newAlbum = new Album(albumName);
                albums.add(newAlbum);
                DataManager.saveAlbums(AlbumView.this, albums);
                albumAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void editAlbum() {
        if (selectedAlbumPosition == -1) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Album");

        final EditText input = new EditText(this);
        input.setText(albums.get(selectedAlbumPosition).getAlbumName());
        builder.setView(input);

        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newAlbumName = input.getText().toString().trim();

                if (newAlbumName.isEmpty()) {
                    Toast.makeText(AlbumView.this, "Album name cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Album album : albums) {
                    if (album.getAlbumName().equalsIgnoreCase(newAlbumName)) {
                        Toast.makeText(AlbumView.this, "Album with this name already exists.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                albums.get(selectedAlbumPosition).setAlbumName(newAlbumName);
                DataManager.saveAlbums(AlbumView.this, albums);
                albumAdapter.notifyDataSetChanged();
            }
        });

        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                albums.remove(selectedAlbumPosition);
                DataManager.saveAlbums(AlbumView.this, albums);
                albumAdapter.notifyDataSetChanged();
                selectedAlbumPosition = -1;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void openAlbum() {
        if (selectedAlbumPosition == -1) {
            return;
        }

        // Create an intent to open the PhotoView activity
        Intent intent = new Intent(AlbumView.this, PhotoView.class);

        // Pass the index of the selected album to the PhotoView activity
        intent.putExtra("albumIndex", selectedAlbumPosition); // Use "albumIndex" as key here

        // Start the PhotoView activity
        startActivity(intent);
    }



}

