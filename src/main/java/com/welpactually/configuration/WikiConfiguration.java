package com.welpactually.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.smartmachine.couchbase.CouchbaseClientFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@SuppressWarnings("UnusedDeclaration")
public class WikiConfiguration extends Configuration {

    @Valid
    @NotNull
    private CouchbaseClientFactory ccf = new CouchbaseClientFactory();

    @JsonProperty("couchbase")
    public CouchbaseClientFactory getCouchbaseClientFactory() {
        return ccf;
    }

    @JsonProperty("couchbase")
    public void setCouchbaseClientFactory(CouchbaseClientFactory ccf) {
        this.ccf = ccf;
    }
}