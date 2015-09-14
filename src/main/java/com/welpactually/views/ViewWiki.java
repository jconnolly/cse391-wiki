package com.welpactually.views;

import com.welpactually.model.Wiki;
import io.dropwizard.views.View;

public class ViewWiki extends View {

    private Wiki wiki;

    public ViewWiki(Wiki wiki) {
        super("wiki.mustache");
        this.wiki = wiki;
    }

    public Wiki getWiki() {
        return wiki;
    }
}
