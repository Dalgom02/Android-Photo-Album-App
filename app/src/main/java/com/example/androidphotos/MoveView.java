package com.example.androidphotos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import model.Album;
import model.DataManager;
import model.Photo;

public class MoveView extends AppCompatActivity {
    private ImageView photoMoveImage;
    private TextView photoMoveTextBox;
    private Spinner photosMovePick;
    private Button confirmMoveButton;

    private int albumIndex;
    private int photoIndex;
    private Album currentAlbum;
    private List<Album> albums;
    private List<Photo> photos;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_move);

        photoMoveTextBox = findViewById(R.id.photoMoveTextBox);
        confirmMoveButton = findViewById(R.id.confirmMoveButton);
        photoMoveImage = findViewById(R.id.photoMoveImage);
        photosMovePick = findViewById(R.id.photosMovePick);

        albumIndex = getIntent().getIntExtra("albumIndex", -1);
        photoIndex = getIntent().getIntExtra("photoIndex", -1);

        currentAlbum = DataManager.loadAlbums(this).get(albumIndex);
        albums = DataManager.loadAlbums(this);
        photos = currentAlbum.getPhotos();


        Photo currPhoto = photos.get(photoIndex);
        Uri photoUri = Uri.parse(currPhoto.getUri());
        photoMoveImage.setImageURI(photoUri);



        ArrayList<String> arrayList = new ArrayList<>();
        for(Album a: albums){
            if(!(a.getAlbumName().equals(currentAlbum.getAlbumName()))){
                arrayList.add(a.getAlbumName());
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,arrayList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        photosMovePick.setAdapter(adapter);

        confirmMoveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String aMT = photosMovePick.getItemAtPosition(photosMovePick.getSelectedItemPosition()).toString();

                int count = 0;
                int albumMoveIndex = 0;
                for(Album a : albums){
                    if(a.getAlbumName().equals(aMT)){
                        albumMoveIndex = count;
                    }
                    count++;
                }

                albums.get(albumMoveIndex).getPhotos().add(currPhoto);

                albums.get(albumIndex).removePhoto(photoIndex);
                DataManager.saveAlbums(MoveView.this, albums);

                Toast.makeText(MoveView.this, "Photo moved successfully", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(MoveView.this, AlbumView.class));
            }

        }
        );


    }

}