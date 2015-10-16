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
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

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
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new RedirectBundle(new PathRedirect("/", "/Main")));
        bootstrap.addBundle(new RedirectBundle(new PathRedirect("/grading/SBU", "/SBU")));
    }

    @Override
    public void run(WikiConfiguration config, Environment environment) {
        WikiDAO dao = new WikiDAO(hibernate.getSessionFactory());
        environment.jersey().register(new Page(dao));
        environment.jersey().register(new LoggingFilter(
                        Logger.getLogger(LoggingFilter.class.getName()),
                        true)
        );

    }

    private HibernateBundle<WikiConfiguration> hibernate = new HibernateBundle<WikiConfiguration>(Wiki.class) {
        public DataSourceFactory getDataSourceFactory(WikiConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };
}