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

import java.util.ArrayList;
import java.util.List;

import model.Album;
import model.DataManager;
import model.Photo;

public class PhotoView extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;
    private Button addPhotoButton;
    private Button editPhotoButton;
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
        editPhotoButton = findViewById(R.id.editPhotoButton);
        openPhotoButton = findViewById(R.id.openPhotoButton);

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
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

        EditText photoNameEditText = view.findViewById(R.id.photo_name_edit_text);
        EditText personTagEditText = view.findViewById(R.id.person_tag_edit_text);
        EditText locationTagEditText = view.findViewById(R.id.location_tag_edit_text);

        builder.setView(view)
                .setPositiveButton("Add Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String photoName = photoNameEditText.getText().toString().trim();
                        String personTag = personTagEditText.getText().toString().trim();
                        String locationTag = locationTagEditText.getText().toString().trim();

                        if (!photoName.isEmpty()) {
                            newPhoto.setPhotoName(photoName);
                        }

                        if (!personTag.isEmpty()) {
                            newPhoto.addPersonTag(personTag);
                        }

                        if (!locationTag.isEmpty()) {
                            newPhoto.addLocationTag(locationTag);
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
