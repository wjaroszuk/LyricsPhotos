package com.lyricsphotos.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class Stanza implements Serializable {
    @Id
    private int id;
    @Lob
    private ArrayList<String> lines;
    @Lob
    private ArrayList<String> tags;

    public Stanza() {
    }

    public Stanza(int id, ArrayList<String> lines) {
        this.id = id;
        this.lines = lines;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getLines() {
        return lines;
    }

    public void setLines(ArrayList<String> lines) {
        this.lines = lines;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
