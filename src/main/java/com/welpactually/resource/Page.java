package com.welpactually.resource;

import com.welpactually.db.WikiDAO;
import com.welpactually.model.Wiki;
import com.welpactually.views.EditWiki;
import com.welpactually.views.ViewWiki;
import io.dropwizard.hibernate.UnitOfWork;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
public class Page {

    private final WikiDAO wikiDao;

    public Page(final WikiDAO wikiDAO) {
        this.wikiDao = wikiDAO;
    }

    @POST
    @UnitOfWork
    @Path("/API/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    private Wiki createWiki(Wiki wiki) {
      return wikiDao.saveOrUpdate(wiki);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/API/{title}")
    @UnitOfWork
    public Wiki getWiki(@PathParam("title") String title) {
      return wikiDao.findByTitle(title);
    }


    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/wiki/{title}")
    @UnitOfWork
    public ViewWiki viewWiki(@PathParam("title") String title) {
        Wiki wiki = wikiDao.findByTitle(title);
        if (wiki != null){
            return new ViewWiki(wiki);
        }
        else
            return new ViewWiki(new Wiki(title, ""));
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/wiki/{title}/edit")
    @UnitOfWork
    public EditWiki editWiki(@PathParam("title") String title) {
        Wiki wiki = wikiDao.findByTitle(title);
        if (wiki != null){
            return new EditWiki(wiki);
        }
        else
            return new EditWiki(new Wiki(title, ""));
    }
}