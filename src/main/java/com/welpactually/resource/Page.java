package com.welpactually.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.welpactually.db.WikiDAO;
import com.welpactually.model.Wiki;
import com.welpactually.model.WikiResponse;
import com.welpactually.views.EditWiki;
import com.welpactually.views.ViewWiki;
import io.dropwizard.hibernate.UnitOfWork;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path(value = "/")
public class Page {

    ObjectMapper mapper = new ObjectMapper();

    private final WikiDAO wikiDao;

    public Page(final WikiDAO wikiDAO) {
        this.wikiDao = wikiDAO;
    }

    @POST
    @UnitOfWork
    @Path("/API/{title}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editWiki(@PathParam("title") String title,
                             @FormDataParam("data") String data) throws IOException {

        Wiki wiki = wikiFromString(data);
        String sanitizedTitle = sanitizeTitle(title);
        if (!sanitizedTitle.equals(wiki.getTitle()))
            return Response.status(Response.Status.BAD_REQUEST).build();

        Wiki found = wikiDao.findByTitle(sanitizedTitle);
        if (found == null) {
            WikiResponse createdResponse = new WikiResponse(wikiDao.createOrUpdate(wiki), "CREATED");
            return Response.ok(createdResponse).build();
        } else {
            found.setBody(wiki.getBody());
            WikiResponse updatedResposne = new WikiResponse(wikiDao.createOrUpdate(found), "UPDATED");
            return Response.ok(updatedResposne).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/API/{title}")
    @UnitOfWork
    public Wiki getWiki(@PathParam("title") String title) {
        return wikiDao.findByTitle(sanitizeTitle(title));
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{title}")
    @UnitOfWork
    public ViewWiki viewWiki(@PathParam("title") String title) {
        Wiki wiki = wikiDao.findByTitle(sanitizeTitle(title));
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
        Wiki wiki = wikiDao.findByTitle(sanitizeTitle(title));
        if (wiki != null) {
            return new EditWiki(wiki);
        } else
            return new EditWiki(new Wiki(title, ""));
    }

    private Wiki wikiFromString(String data) throws IOException {
        return mapper.readValue(data, Wiki.class);
    }

    private String sanitizeTitle(String title) {
        return title.replace('_', ' ');
    }
}