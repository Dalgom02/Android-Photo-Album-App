package model;

import java.io.*;
import java.util.*;

public class Album implements Serializable {

    public static final long serialVersionUID = 1L;

    private String albumName;
    private List<Photo> photos;
    private Photo currentPhoto = null;

    public Album(String albumName) {
        this.albumName = albumName;
        photos = new ArrayList<Photo>();
    }

    public void addPhoto(String photoPath) {
        Photo newPhoto = new Photo(photoPath);
        photos.add(newPhoto);
    }

    public void removePhoto(int photoIndex) {
        photos.remove(photoIndex);
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public Photo getcurrentPhoto() {
        return currentPhoto;
    }

    public void setCurrentPhoto(Photo currentPhoto) {
        this.currentPhoto = currentPhoto;
    }
}
