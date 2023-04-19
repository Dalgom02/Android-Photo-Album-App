package model;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final String ALBUMS_FILE_NAME = "albums.ser";

    public static void saveAlbums(Context context, List<Album> albums) {
        try {
            FileOutputStream fos = context.openFileOutput(ALBUMS_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(albums);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Album> loadAlbums(Context context) {
        List<Album> albums = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(ALBUMS_FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            albums = (List<Album>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return albums;
    }
}

