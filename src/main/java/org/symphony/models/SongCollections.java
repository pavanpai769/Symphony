package org.symphony.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SongCollections {
   private List<Song> songList;
   private Set<String> songTitles;

   public SongCollections(){
       this.songList= new ArrayList<>();
       this.songTitles= new HashSet<>();
   }

   public void addSong(Song song){
       this.songList.add(song);
   }

   public void addTitle(String title){
       this.songTitles.add(title);
   }

   public List<Song> getSongList(){
       return  this.songList;
   }

   public Set<String> getSongTitles() {
       return this.songTitles;
   }

    public void clear() {
       this.songList.clear();
       this.songTitles.clear();
    }
}
