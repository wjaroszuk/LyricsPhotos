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
    private ArrayList<Stanza> stanzas;
    private boolean areImagesGenerated = false;
    private boolean areTagsGenerated = false;


    public Song() {
    }

    public Song(String artist, String title, ArrayList<Stanza> stanzas, boolean areImagesGenerated, boolean areTagsGenerated) {
        this.artist = artist;
        this.title = title;
        this.stanzas = stanzas;
        this.areImagesGenerated = areImagesGenerated;
        this.areTagsGenerated = areTagsGenerated;
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

    public ArrayList<Stanza> getStanzas() {
        return stanzas;
    }

    public void setStanzas(ArrayList<Stanza> stanzas) {
        this.stanzas = stanzas;
    }

    public boolean isAreImagesGenerated() {
        return areImagesGenerated;
    }

    public void setAreImagesGenerated(boolean areImagesGenerated) {
        this.areImagesGenerated = areImagesGenerated;
    }

    public boolean isAreTagsGenerated() {
        return areTagsGenerated;
    }

    public void setAreTagsGenerated(boolean areTagsGenerated) {
        this.areTagsGenerated = areTagsGenerated;
    }


}
