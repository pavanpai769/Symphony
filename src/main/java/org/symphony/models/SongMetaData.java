package org.symphony.models;

import javafx.scene.image.Image;

public class SongMetaData {
	private String title;
	private String artist;
	private String year;
	private String album;
	private String genreDescription;
	private Image albumArt;

	public SongMetaData(String title, String artist,String year,String album,String genreDescription, Image albumArt) {
		this.title = title;
		this.artist = artist;
		this.year=year;
		this.album=album;
		this.genreDescription=genreDescription;
		this.albumArt = albumArt;
	}

	public SongMetaData() {

	}


	public String getTitle() {
		return this.title;
	}

	public String getArtist(){
		return this.artist;
	}


	public Image getAlbumArt() {
		return this.albumArt;
	}

	public String getYear() {
		return year;
	}

	public String getAlbum() {
		return album;
	}

	public String getGenreDescription() {
		return genreDescription;
	}
}
