package org.symphony.models;

public class Song {
    private final String songPath;
    private final SongMetaData metaData;

    public Song(String songPath,SongMetaData metaData){
        this.songPath=songPath;
        this.metaData=metaData;
    }


    public String getSongPath() {
        return songPath;
    }

    public SongMetaData getMetaData() {
        return metaData;
    }
}
