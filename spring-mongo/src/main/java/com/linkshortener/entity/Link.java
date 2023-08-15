package com.linkshortener.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Link {
    @Id
    private String original_link;
    private String link_ref;
    private String belongs_to;
    private int click;

    public String getOriginal_link() {
        return original_link;
    }

    public void setOriginal_link(String original_link) {
        this.original_link = original_link;
    }

    public String getLink_ref() {
        return link_ref;
    }

    public void setLink_ref(String link_ref) {
        this.link_ref = link_ref;
    }

    public String getBelongs_to() {
        return belongs_to;
    }

    public void setBelongs_to(String belongs_to) {
        this.belongs_to = belongs_to;
    }

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }
}