package com.welpactually.resource;

import com.welpactually.db.WikiDAO;
import com.welpactually.model.Wiki;
import info.bliki.wiki.model.WikiModel;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path(value = "/")
public class Page {

    private final WikiDAO wikiDao;

    public Page(final WikiDAO wikiDAO) {
        this.wikiDao = wikiDAO;
    }

    @POST
    @UnitOfWork
    @Path("/{title}")
    public Wiki createWiki(Wiki wiki) {
      return wikiDao.saveOrUpdate(wiki);
    }

    @GET
    @Path("/{title}")
    @UnitOfWork
    public Wiki getWiki(@PathParam("title") String title) {
      return wikiDao.findByTitle(title);
    }

    @GET
    @UnitOfWork
    @Path("/{title}/html")
    public String getWikiHtml(@PathParam("title") String title) {
        Wiki wiki = getWiki(title);
        return WikiModel.toHtml(wiki.getBody());
    }

}