package org.symphony.service;


import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import javafx.scene.image.Image;
import org.symphony.models.SongMetaData;

import java.io.ByteArrayInputStream;


public class MetaDataService{

    public SongMetaData extractMetaData(String filePath) {

        try {
            Mp3File song = new Mp3File(filePath);
            if (song.hasId3v2Tag()) {
                ID3v2 id3v2Tag = song.getId3v2Tag();
                String title = id3v2Tag.getTitle();
                String artist = id3v2Tag.getArtist();
                String album = id3v2Tag.getAlbum();
                String year = id3v2Tag.getYear();
                String genreDescription = id3v2Tag.getGenreDescription();
                Image albumArt=null;
                if (id3v2Tag.getAlbumImage() != null) {
                    albumArt = byteArrayToImage(id3v2Tag.getAlbumImage());
                }
                return new SongMetaData(title,artist,year,album,genreDescription,albumArt);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Image byteArrayToImage(byte[] byteArray) throws Exception {

        try (ByteArrayInputStream bis = new ByteArrayInputStream(byteArray)) {
            return new Image(bis);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
