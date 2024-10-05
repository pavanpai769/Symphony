package org.symphony.models;

import java.util.Stack;

public class PlayedSongList {
    Stack<Song> playedList = new Stack<>();

    public void pushSong(Song song){
        if(song!=null){
            playedList.push(song);
        }
    }

    public Song getSong(){
        return playedList.pop();
    }

    public boolean isEmpty(){
        return playedList.isEmpty();
    }
}
