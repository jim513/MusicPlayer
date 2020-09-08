package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Song {

	private SimpleStringProperty artistName; 	//man
	private SimpleStringProperty songName; 		//1. dream
	private SimpleStringProperty duration;
	private SimpleStringProperty url;			//c:\music\dream

	public Song(String artistName, String songName, String duration, String url) {
		this.artistName = new SimpleStringProperty(artistName);
		this.songName = new SimpleStringProperty(songName);
		this.duration = new SimpleStringProperty(duration);
		this.url = new SimpleStringProperty(url);
	}

	public String getArtistName() {
		return artistName.get();
	}

	public void setArtistName(String artistName) {
		this.artistName.set(artistName);
	}

	public StringProperty artistNameProperty() {
		return artistName;
	}

	public String getSongName() {
		return songName.get();
	}

	public void setSongName(String songName) {
		this.songName.set(songName);
	}

	public StringProperty songNameProperty() {
		return songName;
	}

	public String getDuration() {
		return duration.get();
	}

	public StringProperty durationProperty() {
		return duration;
	}

	public void setDuration(String rate) {
		this.duration.set(rate);
	}

	public String getUrl() {
		return url.get();
	}

	public StringProperty urlProperty() {
		return url;
	}

	public void setUrl(String url) {
		this.url.set(url);
	}

	@Override
	public String toString(){
		return this.getSongName() + " " + this.getArtistName() + " " + this.getDuration();
	}
}
