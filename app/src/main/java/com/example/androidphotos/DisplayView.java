package com.example.androidphotos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Album;
import model.DataManager;
import model.Photo;

public class DisplayView extends AppCompatActivity {

    private ImageView photoDisplayImage;
    private TextView locationTagTextView;
    private TextView peopleTagTextView;
    private Button previousButton;
    private Button nextButton;

    private Button addTagButton;

    private Button deleteTagButton;

    private int albumIndex;
    private int photoIndex;
    private Album currentAlbum;
    private List<Photo> photos;

    private List<Album> albums;

    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_display);

        photoDisplayImage = findViewById(R.id.photo_display_image);
        locationTagTextView = findViewById(R.id.location_tag_text_view);
        peopleTagTextView = findViewById(R.id.people_tag_text_view);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        addTagButton = (Button) findViewById(R.id.addTagButton);
        deleteTagButton = findViewById(R.id.deleteTagButton);

        albumIndex = getIntent().getIntExtra("albumIndex", -1);
        photoIndex = getIntent().getIntExtra("photoIndex", -1);
        albums = DataManager.loadAlbums(this);

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

        addTagButton.setOnClickListener(v->{
            Photo p = photos.get(photoIndex);
            addTagDialog(p);
            //startActivity(new Intent(DisplayView.this, PhotoView.class));
        });


        deleteTagButton.setOnClickListener(v->{
            Photo p = photos.get(photoIndex);
            deleteTagDialog(p);
            //startActivity(new Intent(DisplayView.this, PhotoView.class));
        });


    }
    private void deleteTagDialog(Photo p){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_delete_tag,null);
        Spinner deleteTagSpin = view.findViewById(R.id.deleteSpin);

        ArrayList<String> pTags = new ArrayList<>();
        if(!(p.getPersonTags().isEmpty())){
            for(String s: p.getPersonTags()){
                String m = "Person " + s;
                pTags.add(m);
            }
        }
        if(!(p.getLocationTags().isEmpty())){
            for(String sa : p.getLocationTags()){
                String l = "Location " + sa;
                pTags.add(l);
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,pTags);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deleteTagSpin.setAdapter(adapter);


        builder.setView(view)
                .setPositiveButton("Delete Tag", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String delTag = deleteTagSpin.getItemAtPosition(deleteTagSpin.getSelectedItemPosition()).toString();
                String[] split = delTag.split("\\s+");

                if(split[0].equalsIgnoreCase("Location")){
                   // currentAlbum.getPhotos().get(photoIndex).getLocationTags().remove(split[1]);
                    p.getLocationTags().remove(split[1]);
                }
                if(split[0].equalsIgnoreCase("Person")) {
                    p.getPersonTags().remove(split[1]);
                }
                DataManager.saveAlbums(DisplayView.this, albums);
                updatePhotoDisplay();
                Toast.makeText(DisplayView.this, "Tag deleted successfully", Toast.LENGTH_SHORT).show();
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


    private void addTagDialog (Photo p){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_photo, null);
        EditText personTagEditText = view.findViewById(R.id.person_tag_edit_text);
        EditText locationTagEditText = view.findViewById(R.id.location_tag_edit_text);

        builder.setView(view)
                .setPositiveButton("Add Tag", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(personTagEditText.getText() == null && locationTagEditText.getText() == null) {
                            Toast.makeText(DisplayView.this, "ERROR BOTH FIELDS CANNOT BE EMPTY", Toast.LENGTH_SHORT).show();
                        }
                        else{

                            String personTags = personTagEditText.getText().toString().trim();
                            String locationTags = locationTagEditText.getText().toString().trim();

                            if (!personTags.isEmpty()) {
                                String[] personTagArray = personTags.split(",");
                                Set<String> uniquePersonTags = new HashSet<>();
                                for (String tag : personTagArray) {
                                    uniquePersonTags.add(tag.trim());
                                }

                                for (String uniqueTag : uniquePersonTags) {
                                    p.addPersonTag(uniqueTag);
                                }
                            }

                            if (!locationTags.isEmpty()) {
                                String[] locationTagArray = locationTags.split(",");
                                Set<String> uniqueLocationTags = new HashSet<>();
                                for (String tag : locationTagArray) {
                                    uniqueLocationTags.add(tag.trim());
                                }

                                for (String uniqueTag : uniqueLocationTags) {
                                            p.addLocationTag(uniqueTag);
                                }
                            }


                        }

                        updatePhotoDisplay();
                        DataManager.saveAlbums(DisplayView.this,albums);

                        Toast.makeText(DisplayView.this, "Tag added successfully", Toast.LENGTH_SHORT).show();
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

    private void updatePhotoDisplay() {
        //photos = DataManager.loadAlbums(this).get(albumIndex).getPhotos();
        Photo currentPhoto = photos.get(photoIndex);

        Uri photoUri = Uri.parse(currentPhoto.getUri());
        photoDisplayImage.setImageURI(photoUri);

        String locationTags = "Location: " + String.join(", ", currentPhoto.getLocationTags());
        locationTagTextView.setText(locationTags);

        String peopleTags = "People: " + String.join(", ", currentPhoto.getPersonTags());
        peopleTagTextView.setText(peopleTags);
    }
}
