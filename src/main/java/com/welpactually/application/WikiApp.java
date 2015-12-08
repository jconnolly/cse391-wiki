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
import io.smartmachine.couchbase.CouchbaseClientFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.glassfish.jersey.filter.LoggingFilter;
import io.smartmachine.couchbase.CouchbaseBundle;

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
        bootstrap.addBundle(couchbaseBundle);
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new RedirectBundle(new PathRedirect("/", "/Main")));
    }

    @Override
    public void run(WikiConfiguration config, Environment environment) {
        SolrClient solr = getSolrConnection();
        environment.jersey().register(new Page(solr));
        environment.jersey().register(new LoggingFilter(
                        Logger.getLogger(LoggingFilter.class.getName()),
                        true)
        );

    }

    private final CouchbaseBundle<WikiConfiguration> couchbaseBundle = new CouchbaseBundle<WikiConfiguration>() {
        public CouchbaseClientFactory getCouchbaseClientFactory(WikiConfiguration configuration) {
            return configuration.getCouchbaseClientFactory();
        }
    };

    public SolrClient getSolrConnection() {
        return new HttpSolrClient("http://solr0.local:8983/solr/wiki");
    }
}