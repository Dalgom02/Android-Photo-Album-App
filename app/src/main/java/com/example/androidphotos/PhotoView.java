package com.example.androidphotos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Album;
import model.DataManager;
import model.Photo;

public class PhotoView extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;
    private Button addPhotoButton;

    private Button dPhotoButton;

    private Button movePhotoButton;
    private Button openPhotoButton;

    private int albumIndex;
    private Album currentAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        albumIndex = getIntent().getIntExtra("albumIndex", -1);

        if (albumIndex == -1) {
            Toast.makeText(this, "Invalid album index", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        List<Album> albums = DataManager.loadAlbums(this);
        currentAlbum = albums.get(albumIndex);

        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        photoAdapter = new PhotoAdapter(this, currentAlbum.getPhotos());
        photoRecyclerView.setAdapter(photoAdapter);

        addPhotoButton = findViewById(R.id.addPhotoButton);
        dPhotoButton = findViewById(R.id.dPhotoButton);
        movePhotoButton = findViewById(R.id.movePhotoButton);
        openPhotoButton = findViewById(R.id.openPhotoButton);

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        dPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = photoAdapter.getSelectedPosition();
                if (selectedPosition != -1) {
                    List<Album> albums = DataManager.loadAlbums(PhotoView.this);
                    albums.get(albumIndex).removePhoto(selectedPosition);
                    DataManager.saveAlbums(PhotoView.this, albums);
                    currentAlbum = albums.get(albumIndex);
                    photoAdapter.setPhotos(currentAlbum.getPhotos());
                    photoAdapter.notifyDataSetChanged();
                    Toast.makeText(PhotoView.this, "Photo deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PhotoView.this, "No photo selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        movePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = photoAdapter.getSelectedPosition();
                if (selectedPosition != -1) {
                    // Edit the selected photo
                    Intent i = new Intent(PhotoView.this, MoveView.class);
                    i.putExtra("albumIndex", albumIndex);
                    i.putExtra("photoIndex",photoAdapter.getSelectedPosition());
                    startActivity(i);
                } else {
                    Toast.makeText(PhotoView.this, "No photo selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ...
        openPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = photoAdapter.getSelectedPosition();
                if (selectedPosition != -1) {
                    Intent intent = new Intent(PhotoView.this, DisplayView.class);
                    intent.putExtra("albumIndex", albumIndex);
                    intent.putExtra("photoIndex", selectedPosition);
                    startActivity(intent);
                } else {
                    Toast.makeText(PhotoView.this, "No photo selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Photo newPhoto = new Photo(System.currentTimeMillis(), imageUri);
            DataManager.saveAlbums(this, DataManager.loadAlbums(this));
            showAddPhotoDialog(newPhoto);
        }
    }

    private void showAddPhotoDialog(Photo newPhoto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_photo, null);

        EditText personTagEditText = view.findViewById(R.id.person_tag_edit_text);
        EditText locationTagEditText = view.findViewById(R.id.location_tag_edit_text);

        builder.setView(view)
                .setPositiveButton("Add Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String personTags = personTagEditText.getText().toString().trim();
                        String locationTags = locationTagEditText.getText().toString().trim();

                        if (!personTags.isEmpty()) {
                            String[] personTagArray = personTags.split(",");
                            Set<String> uniquePersonTags = new HashSet<>();
                            for (String tag : personTagArray) {
                                uniquePersonTags.add(tag.trim());
                            }

                            for (String uniqueTag : uniquePersonTags) {
                                newPhoto.addPersonTag(uniqueTag);
                            }
                        }

                        if (!locationTags.isEmpty()) {
                            String[] locationTagArray = locationTags.split(",");
                            Set<String> uniqueLocationTags = new HashSet<>();
                            for (String tag : locationTagArray) {
                                uniqueLocationTags.add(tag.trim());
                            }

                            for (String uniqueTag : uniqueLocationTags) {
                                newPhoto.addLocationTag(uniqueTag);
                            }
                        }

                        List<Album> albums = DataManager.loadAlbums(PhotoView.this);
                        albums.get(albumIndex).getPhotos().add(newPhoto);
                        DataManager.saveAlbums(PhotoView.this, albums);
                        currentAlbum = albums.get(albumIndex);
                        photoAdapter.setPhotos(currentAlbum.getPhotos());
                        photoAdapter.notifyDataSetChanged();
                        Toast.makeText(PhotoView.this, "Photo added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }





}