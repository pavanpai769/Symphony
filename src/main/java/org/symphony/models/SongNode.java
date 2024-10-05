package org.symphony.models;

public class SongNode {
    public Song song;
    public SongNode nextSong;

    public SongNode(Song song) {
        this.song = song;
        this.nextSong = null;
    }

}
