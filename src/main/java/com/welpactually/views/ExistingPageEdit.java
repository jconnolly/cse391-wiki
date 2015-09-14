package com.welpactually.views;

import com.welpactually.model.Wiki;
import io.dropwizard.views.View;

public class ExistingPageEdit extends View {
    private Wiki wiki;

    public ExistingPageEdit(Wiki wiki) {
        super("edit.mustache");
        this.wiki = wiki;
    }
}
