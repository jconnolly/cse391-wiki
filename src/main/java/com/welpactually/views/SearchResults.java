package com.welpactually.views;

import com.welpactually.model.Wiki;
import io.dropwizard.views.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResults extends View {

    private String query;


    private List results;

    public SearchResults(String query, ArrayList results) {
        super("results.mustache");
        this.query = query;
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public List getResults() {
        return results;
    }
}
