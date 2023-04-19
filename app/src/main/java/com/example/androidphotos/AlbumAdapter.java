package com.example.androidphotos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import model.Album;

public class AlbumAdapter extends ArrayAdapter<Album> {

    private final int layoutResource;

    public AlbumAdapter(@NonNull Context context, int resource, @NonNull List<Album> objects) {
        super(context, resource, objects);
        this.layoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(layoutResource, parent, false);
        }

        TextView albumNameTextView = convertView.findViewById(android.R.id.text1);
        Album album = getItem(position);
        if (album != null) {
            albumNameTextView.setText(album.getAlbumName());
        }

        return convertView;
    }
}
