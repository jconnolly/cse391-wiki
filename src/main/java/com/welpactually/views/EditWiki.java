package com.welpactually.views;

import com.welpactually.model.Wiki;
import io.dropwizard.views.View;

public class EditWiki extends View {
    private Wiki wiki;

    public EditWiki(Wiki wiki) {
        super("edit.mustache");
        this.wiki = wiki;
    }

    public Wiki getWiki(){
        return wiki;
    }
}
