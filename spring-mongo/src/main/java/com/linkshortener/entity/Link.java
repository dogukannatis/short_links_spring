package com.linkshortener.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "links")
public class Link {
    @Id
    private String id;
    private String original_link;
    private String link_ref;
    private String belongs_to;
    private int click;

    public Link(String original_link, String link_ref, String belongs_to, int click) {
        this.original_link = original_link;
        this.link_ref = link_ref;
        this.belongs_to = belongs_to;
        this.click = click;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
