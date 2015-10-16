package com.welpactually.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.bliki.wiki.dump.WikiPatternMatcher;
import info.bliki.wiki.model.WikiModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "wiki")
@NamedQueries({
        @NamedQuery(
                name = "com.welpactually.model.Wiki.findAll",
                query = "select w from Wiki w "
        )
})
public class Wiki {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "redirect", nullable = true)
    private String redirect;

    public Wiki() {
    }

    public Wiki(String title, String body) {
        this.title = title;
        this.body = body;
    }
    @JsonProperty
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Wiki)) {
            return false;
        }

        final Wiki that = (Wiki) o;

        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.title, that.title) &&
                Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body);
    }

    @Override
    public String toString() {
        return "Wiki{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getRedirect() {
        return this.redirect;
    }
}