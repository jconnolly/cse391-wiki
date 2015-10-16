package com.welpactually.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.welpactually.db.WikiDAO;
import com.welpactually.model.Wiki;
import com.welpactually.model.WikiResponse;
import com.welpactually.views.EditWiki;
import com.welpactually.views.MoveWiki;
import com.welpactually.views.ViewWiki;
import io.dropwizard.hibernate.UnitOfWork;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
    public Response editWiki(@PathParam("title") String urlTitle,
                             @FormDataParam("data") String data) throws IOException, URISyntaxException {

        Wiki wiki = wikiFromString(data);
        String sanitizedUrlTitle = sanitizeTitle(urlTitle);
        Wiki found = wikiDao.findByTitle(sanitizedUrlTitle);
        if (shouldMove(wiki, sanitizedUrlTitle, found)) {
            return Response.temporaryRedirect(new URI(move(found, wiki).getRedirect())).build();
        } else if(shouldCreate(wiki, sanitizedUrlTitle, found)) {
            WikiResponse createdResponse = new WikiResponse(wikiDao.createOrUpdate(wiki), "CREATED");
            return Response.ok(createdResponse).build();
        } else if (shouldUpdate(wiki, sanitizedUrlTitle, found)) {
            found.setBody(wiki.getBody());
            WikiResponse updatedResposne = new WikiResponse(wikiDao.createOrUpdate(found), "UPDATED");
            return Response.ok(updatedResposne).build();
        } else
            return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private boolean shouldMove(Wiki wiki, String sanitizedUrlTitle, Wiki found) {
        return !sanitizedUrlTitle.equals(wiki.getTitle()) && found != null;
    }

    private boolean shouldCreate(Wiki wiki, String sanitizedUrlTitle, Wiki found) {
        return sanitizedUrlTitle.equals(wiki.getTitle()) && found == null;
    }

    private boolean shouldUpdate(Wiki wiki, String sanitizedUrlTitle, Wiki found) {
        return sanitizedUrlTitle.equals(wiki.getTitle()) && found != null;
    }

    private Wiki move(Wiki source, Wiki destination) {
        wikiDao.createOrUpdate(destination);
        source.setRedirect(destination.getTitle());
        return wikiDao.createOrUpdate(source);
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
    public Response viewWiki(@PathParam("title") String title) throws URISyntaxException {
        Wiki wiki = wikiDao.findByTitle(sanitizeTitle(title));

        if (wiki.getRedirect()!=null)
            return Response.temporaryRedirect(new URI(wiki.getRedirect())).build();
        if (wiki != null) {
            return Response.ok(new ViewWiki(wiki)).build();
        } else
            return Response.ok(new ViewWiki(new Wiki(title, ""))).build();
    }



    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{title}/edit")
    @UnitOfWork
    public Response editWiki(@PathParam("title") String title) throws URISyntaxException {
        Wiki wiki = wikiDao.findByTitle(sanitizeTitle(title));
        if (wiki.getRedirect()!=null)
            return Response.temporaryRedirect(new URI(wiki.getRedirect())).build();
        if (wiki != null) {
            return Response.ok(new EditWiki(wiki)).build();
        } else
            return Response.ok(new ViewWiki(new Wiki(title, ""))).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{title}/move")
    @UnitOfWork
    public Response moveWiki(@PathParam("title") String title) throws URISyntaxException {
        Wiki wiki = wikiDao.findByTitle(sanitizeTitle(title));

        if (wiki.getRedirect()!=null)
            return Response.temporaryRedirect(new URI(wiki.getRedirect())).build();
        if (wiki != null) {
            return Response.ok(new MoveWiki(wiki)).build();
        } else
            return Response.ok(new ViewWiki(new Wiki(title, ""))).build();
    }

    private Wiki wikiFromString(String data) throws IOException {
        return mapper.readValue(data, Wiki.class);
    }

    private String sanitizeTitle(String title) {
        return title.replace('_', ' ');
    }
}