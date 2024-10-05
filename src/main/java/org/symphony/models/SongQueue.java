package org.symphony.models;


public class SongQueue {
    private SongNode startingSong;

    public void addToQueue(Song song) {
        if(song==null) return;

        if (startingSong == null) {
            startingSong = new SongNode(song);
            return;
        }

        SongNode currSong = startingSong;
        while (currSong.nextSong != null) {
            currSong = currSong.nextSong;
        }

        currSong.nextSong = new SongNode(song);
    }

    public void addFirstToQueue(Song song) {
        if(song==null) return;

        SongNode newSong = new SongNode(song);
        if (startingSong == null) {
            startingSong = newSong;
            return;
        }
        newSong.nextSong = startingSong;
        startingSong = newSong;
    }

    public void removeSong(Song song) {
        if(song==null) return;

        if (startingSong == null) return;

        SongNode previousSongNode = null;
        SongNode currSongNode = startingSong;

        while(currSongNode!=null && currSongNode.song!=song){
            previousSongNode=currSongNode;
            currSongNode=currSongNode.nextSong;
        }

        if(currSongNode==null) return;

        if(previousSongNode!=null) {
            previousSongNode.nextSong = currSongNode.nextSong;
        }else{
            startingSong=startingSong.nextSong;
        }

    }


    public void insertSongAfterCurrent(Song currentPlayingSong, Song newSong) {
        if (newSong == null) return;

        if (currentPlayingSong == null || startingSong == null) {
            startingSong = new SongNode(newSong);
            return;
        }

        SongNode currSongNode = startingSong;

        while (currSongNode != null && currSongNode.song != currentPlayingSong) {
            currSongNode = currSongNode.nextSong;
        }


        if (currSongNode == null) return;

        SongNode newSongNode = new SongNode(newSong);

        newSongNode.nextSong = currSongNode.nextSong;

        currSongNode.nextSong=newSongNode;
    }

    public void removeAllSongs(){
        startingSong=null;
    }

    public SongNode getFirstSong(){
        return startingSong;
    }

    public Song getNextSong(Song currentoSong){
         if(currentoSong == null) return null;

         if(startingSong==null) return null;

         SongNode currSongNode = startingSong;
         while(currSongNode!=null && currSongNode.song!=currentoSong){
             currSongNode=currSongNode.nextSong;
         }

         if(currSongNode==null) return null;

         if(currSongNode.nextSong==null) return null;

         return currSongNode.nextSong.song;
    }

    public SongNode getCurrentSongNode(Song currentSong){
        if(currentSong==null) return null;

        SongNode currSongNode = startingSong;

        while(currSongNode!=null && currSongNode.song!=currentSong){
            currSongNode=currSongNode.nextSong;
        }

        return currSongNode;
    }

    public void removeFirstSong() {
        if(startingSong==null)return;
        startingSong=startingSong.nextSong;
    }
}
