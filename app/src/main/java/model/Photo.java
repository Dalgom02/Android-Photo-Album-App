package model;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {

    public static final long serialVersionUID = 1L;

    private long id;
    private String uri;
    private String photoName;
    private List<String> personTags;
    private List<String> locationTags;

    public Photo(String photoName) {
        this.photoName = photoName;
        personTags = new ArrayList<String>();
        locationTags = new ArrayList<String>();
    }

    public Photo(long id, Uri photoUri) {
        this.id = id;
        this.uri = photoUri.toString();
        personTags = new ArrayList<String>();
        locationTags = new ArrayList<String>();
    }

    public long getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public List<String> getPersonTags() {
        return personTags;
    }

    public void addPersonTag(String personTag) {
        personTags.add(personTag);
    }

    public List<String> getLocationTags() {
        return locationTags;
    }

    public void addLocationTag(String locationTag) {
        locationTags.add(locationTag);
    }

    public boolean hasTag(String tagString) {
        String[] tagParts = tagString.toLowerCase().split("=");

        if (tagParts.length != 2) {
            return false;
        }

        String tagType = tagParts[0].trim();
        String tagValue = tagParts[1].trim();

        if (tagType.equals("person")) {
            for (String personTag : personTags) {
                if (personTag.equalsIgnoreCase(tagValue)) {
                    return true;
                }
            }
        } else if (tagType.equals("location")) {
            for (String locationTag : locationTags) {
                if (locationTag.equalsIgnoreCase(tagValue)) {
                    return true;
                }
            }
        }

        return false;
    }

}
