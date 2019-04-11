package com.lyricsphotos.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class LineWithTag implements Serializable {
    @Id
    private int id;
    private String line;
    private String tag;

    public LineWithTag() {
    }

    public LineWithTag(String line, String tag) {
        this.line = line;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
