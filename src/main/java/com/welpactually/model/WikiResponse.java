package com.welpactually.model;

public class WikiResponse {

    private Wiki wiki;
    private String status;

    public WikiResponse(Wiki wiki, String status){
        this.wiki = wiki;
        this.status = status;
    }

    public Wiki getWiki() {
        return wiki;
    }

    public void setWiki(Wiki wiki) {
        this.wiki = wiki;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
