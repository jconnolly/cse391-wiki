package com.welpactually.resource;

import com.welpactually.db.WikiDAO;
import com.welpactually.model.Wiki;
import com.welpactually.model.WikiResponse;
import com.welpactually.views.EditWiki;
import com.welpactually.views.ViewWiki;
import io.dropwizard.hibernate.UnitOfWork;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public Response editWiki(@PathParam("title") String title,
                             Wiki wiki) {
        String sanitizedTitle = sanitizeTitle(title);
        if (!sanitizedTitle.equals(wiki.getTitle()))
            return Response.status(Response.Status.BAD_REQUEST).build();

        Wiki found = wikiDao.findByTitle(sanitizedTitle);
        if (found == null) {
            WikiResponse createdResponse = new WikiResponse(wikiDao.createOrUpdate(wiki), "created");
            return Response.ok(createdResponse).build();
        } else {
            found.setBody(wiki.getBody());
            WikiResponse updatedResposne = new WikiResponse(wikiDao.createOrUpdate(found), "updated");
            return Response.ok(updatedResposne).build();
        }
    }

    private String sanitizeTitle(String title) {
        return title.replace('_', ' ');
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
    @Path("/{title}")
    @UnitOfWork
    public ViewWiki viewWiki(@PathParam("title") String title) {
        Wiki wiki = wikiDao.findByTitle(title);
        if (wiki != null) {
            return new ViewWiki(wiki);
        } else
            return new ViewWiki(new Wiki(title, ""));
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{title}/edit")
    @UnitOfWork
    public EditWiki editWiki(@PathParam("title") String title) {
        Wiki wiki = wikiDao.findByTitle(title);
        if (wiki != null) {
            return new EditWiki(wiki);
        } else
            return new EditWiki(new Wiki(title, ""));
    }
}