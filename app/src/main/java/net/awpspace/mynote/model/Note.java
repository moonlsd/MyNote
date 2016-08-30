package net.awpspace.mynote.model;

/**
 * Created by tuanhai on 8/29/16.
 */
public class Note {
    private String content;

    public Note() {
    }

    public Note(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
