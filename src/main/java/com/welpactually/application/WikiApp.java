package com.welpactually.application;

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
        bootstrap.addBundle(hibernate);
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
    }

    @Override
    public void run(WikiConfiguration config, Environment environment) {
        final WikiDAO dao = new WikiDAO(hibernate.getSessionFactory());
        environment.jersey().register(new Page(dao));
        environment.jersey().setUrlPattern("/API/*");
    }

    private final HibernateBundle<WikiConfiguration> hibernate = new HibernateBundle<WikiConfiguration>(Wiki.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(WikiConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };
}