package com.welpactually.views;

import com.welpactually.model.Wiki;
import io.dropwizard.views.View;

public class MoveWiki extends View {
    private Wiki wiki;

    public MoveWiki(Wiki wiki) {
        super("move.mustache");
        this.wiki = wiki;
    }

    public Wiki getWiki(){
        return wiki;
    }
}
