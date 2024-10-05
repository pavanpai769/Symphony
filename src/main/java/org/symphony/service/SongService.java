package org.symphony.service;

import org.symphony.models.Song;
import org.symphony.models.SongCollections;
import org.symphony.models.SongMetaData;

import java.util.*;

public class SongService {
    private MetaDataService metaDataService;
    private Set<String> songSet;

    public SongService(){
        metaDataService=new MetaDataService();
        songSet= new HashSet<>();
    }


    public String getSongPathByTitle(SongCollections songCollections,String title) throws NoSuchElementException{
        List<Song> songsList = songCollections.getSongList();
        for(Song song:songsList){
            if(song.getMetaData().getTitle().equals(title)){
                return song.getSongPath();
            }
        }
        throw new NoSuchElementException("No song found with title: " + title);
    }

    public SongMetaData getSongMetadataByPath(String filePath) {

        return metaDataService.extractMetaData(filePath);
    }


    public String getSongTitleByMetaData(SongMetaData metaDataOfTheSong) {
        return metaDataOfTheSong.getTitle();
    }

    public void addSongToCollections(SongCollections songCollections, Song song) {
        songCollections.addSong(song);
    }

    public Song getSong(SongCollections songCollections,String songTitle) {
        List<Song> songList=songCollections.getSongList();
        for(Song song:songList){
            if(song.getMetaData().getTitle().equals(songTitle)){
                return song;
            }
        }
        return null;
    }

    public void addTitleToSongCollection(SongCollections songCollections,String songTitle){
        songCollections.addTitle(songTitle);
    }

    public boolean contains(SongCollections songCollections,String songTitle) {
        if(songTitle==null || songCollections==null) return false;
        return songCollections.getSongTitles().contains(songTitle);
    }

    public void clearSongList(SongCollections songCollections) {
        songCollections.clear();
    }
}
