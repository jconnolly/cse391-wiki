package com.welpactually.application;

import com.bazaarvoice.dropwizard.redirect.PathRedirect;
import com.bazaarvoice.dropwizard.redirect.RedirectBundle;
import com.welpactually.configuration.WikiConfiguration;
import com.welpactually.db.WikiDAO;
import com.welpactually.model.Wiki;
import com.welpactually.resource.Page;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class WikiApp extends Application<WikiConfiguration>
{
    public static void main(String[] args) throws Exception
    {
        new WikiApp().run(args);
    }

    @Override
    public String getName() {
        return "CSE391 Wiki";
    }


    @Override
    public void initialize(Bootstrap<WikiConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/assets/", "index.html"));
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new RedirectBundle(new PathRedirect("/", "/wiki/Main")));
    }

    @Override
    public void run(WikiConfiguration config, Environment environment) {
        WikiDAO dao = new WikiDAO(hibernate.getSessionFactory());
        environment.jersey().register(new Page(dao));
    }

    private HibernateBundle<WikiConfiguration> hibernate = new HibernateBundle<WikiConfiguration>(Wiki.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(WikiConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };
}