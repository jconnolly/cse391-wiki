package com.welpactually.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.bliki.wiki.model.WikiModel;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Objects;

public class WikiCB {

    private String title;

    private String body;

    private String redirect;

    public WikiCB() {

    }

    @JsonCreator
    public WikiCB(@JsonProperty("title") String title, @JsonProperty("wiki") String body) {
        this.title = title;
        this.body = body;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }

    @JsonProperty
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public String getBody() {
        return body;
    }

    @JsonProperty
    public void setBody(String body) {
        this.body = body;
    }

    public String toHtml() {
        return WikiModel.toHtml(this.body);
    }

    @JsonProperty
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    @JsonProperty
    public String getRedirect() {
        return this.redirect;
    }
}