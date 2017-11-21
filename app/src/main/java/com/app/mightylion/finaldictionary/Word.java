package com.app.mightylion.finaldictionary;

/**
 * Created by nminh on 11/22/2017.
 */

public class Word {
    private long id;
    private String word;
    private String content;

    public Word() {
        id = 0;
        word = "";
        content = "";
    }

    public Word(long id, String word, String content) {
        this.id = id;
        this.word = word;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
