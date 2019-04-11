package com.lyricsphotos.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class SongWithTags implements Serializable {
    @Id
    private int id;
    private String artist;
    private String title;
    @Lob
    private ArrayList<LineWithTag> linesWithTags;

    public SongWithTags() {
    }

    public SongWithTags(Song song, ArrayList<LineWithTag> linesWithTags) {
        this.artist = song.getArtist();
        this.title = song.getTitle();
        this.linesWithTags = linesWithTags;
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

    public ArrayList<LineWithTag> getLinesWithTags() {
        return linesWithTags;
    }

    public void setLinesWithTags(ArrayList<LineWithTag> linesWithTags) {
        this.linesWithTags = linesWithTags;
    }
}
