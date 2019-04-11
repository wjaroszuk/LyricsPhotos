package com.lyricsphotos.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class Song implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    private String artist;
    private String title;
    @Lob
    private ArrayList<String> lyrics;

    public Song() {
    }

    public Song(String artist, String title, ArrayList<String> lyrics) {
        this.artist = artist;
        this.title = title;
        this.lyrics = lyrics;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getLyrics() {
        return lyrics;
    }

    public void setLyrics(ArrayList<String> lyrics) {
        this.lyrics = lyrics;
    }
}
