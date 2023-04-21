package com.example.androidphotos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import model.Album;
import model.DataManager;
import model.Photo;

public class DisplayView extends AppCompatActivity {

    private ImageView photoDisplayImage;
    private TextView locationTagTextView;
    private TextView peopleTagTextView;
    private Button previousButton;
    private Button nextButton;

    private int albumIndex;
    private int photoIndex;
    private Album currentAlbum;
    private List<Photo> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        photoDisplayImage = findViewById(R.id.photo_display_image);
        locationTagTextView = findViewById(R.id.location_tag_text_view);
        peopleTagTextView = findViewById(R.id.people_tag_text_view);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);

        albumIndex = getIntent().getIntExtra("albumIndex", -1);
        photoIndex = getIntent().getIntExtra("photoIndex", -1);

        if (albumIndex == -1 || photoIndex == -1) {
            finish();
            return;
        }

        currentAlbum = DataManager.loadAlbums(this).get(albumIndex);
        photos = currentAlbum.getPhotos();

        updatePhotoDisplay();

        previousButton.setOnClickListener(v -> {
            if (photoIndex == 0) {
                photoIndex = photos.size() - 1;
            } else {
                photoIndex--;
            }
            updatePhotoDisplay();
        });

        nextButton.setOnClickListener(v -> {
            if (photoIndex == photos.size() - 1) {
                photoIndex = 0;
            } else {
                photoIndex++;
            }
            updatePhotoDisplay();
        });
    }


    private void updatePhotoDisplay() {
        Photo currentPhoto = photos.get(photoIndex);
        Uri photoUri = Uri.parse(currentPhoto.getUri());
        photoDisplayImage.setImageURI(photoUri);

        String locationTags = "Location: " + String.join(", ", currentPhoto.getLocationTags());
        locationTagTextView.setText(locationTags);

        String peopleTags = "People: " + String.join(", ", currentPhoto.getPersonTags());
        peopleTagTextView.setText(peopleTags);
    }
}
