package com.example.androidphotos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import model.Album;
import model.DataManager;
import model.Photo;

public class PhotoView extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private Album album;
    private PhotoAdapter photoAdapter;
    private RecyclerView photoRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        // Load the album data
        List<Album> albums = DataManager.loadAlbums(this);
        album = albums.get(0); // Assuming the first album is the one you want to work with

        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        photoAdapter = new PhotoAdapter(this, album.getPhotos());
        photoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        photoRecyclerView.setAdapter(photoAdapter);

        Button addPhotoButton = findViewById(R.id.addPhotoButton);
        addPhotoButton.setOnClickListener(v -> openImagePicker());

        Button editPhotoButton = findViewById(R.id.editPhotoButton);
        editPhotoButton.setOnClickListener(v -> {
            // Implement edit functionality
        });

        Button openPhotoButton = findViewById(R.id.openPhotoButton);
        openPhotoButton.setOnClickListener(v -> {
            // Implement open functionality
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                showAddPhotoDialog(imageUri);
            }
        }
    }

    private void showAddPhotoDialog(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_photo, null);

        EditText photoNameEditText = view.findViewById(R.id.photo_name_edit_text);
        EditText personTagEditText = view.findViewById(R.id.person_tag_edit_text);
        EditText locationTagEditText = view.findViewById(R.id.location_tag_edit_text);
        Button addPhotoDialogButton = view.findViewById(R.id.add_photo_dialog_button);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        addPhotoDialogButton.setOnClickListener(v -> {
            String photoName = photoNameEditText.getText().toString();
            String personTag = personTagEditText.getText().toString();
            String locationTag = locationTagEditText.getText().toString();

            long photoId = getPhotoId(imageUri);
            Photo photo = new Photo(photoId, imageUri);
            photo.setPhotoName(photoName);
            photo.addPersonTag(personTag);
            photo.addLocationTag(locationTag);
            album.getPhotos().add(photo);
            photoAdapter.notifyItemInserted(album.getPhotos().size() - 1);
            dialog.dismiss();

            // Save the updated album data
            List<Album> albums = DataManager.loadAlbums(this);
            albums.set(0, album); // Assuming the first album is the one you want to work with
            DataManager.saveAlbums(this, albums);
        });

        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save the album data when the activity goes into the background
        List<Album> albums = DataManager.loadAlbums(this);
        albums.set(0, album); // Assuming the first album is the one you want to work with
        DataManager.saveAlbums(this, albums);
    }

    private long getPhotoId(Uri imageUri) {
        String[] projection = {MediaStore.Images.Media._ID};
        Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            long id = cursor.getLong(columnIndex);
            cursor.close();
            return id;
        }

        return -1;
    }



}
