package com.welpactually.resource;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.welpactually.model.Wiki;
import com.welpactually.model.WikiAccessor;
import com.welpactually.model.WikiResponse;
import com.welpactually.views.EditWiki;
import com.welpactually.views.MoveWiki;
import com.welpactually.views.SearchResults;
import com.welpactually.views.ViewWiki;
import info.bliki.wiki.model.WikiModel;
import io.smartmachine.couchbase.Accessor;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Path(value = "/")
public class Page {


    ObjectMapper mapper = new ObjectMapper();

    private final SolrClient solrClient;

    private Logger logger = LoggerFactory.getLogger(Page.class);

    @Accessor
    private WikiAccessor accessor;

    public Page(SolrClient solr) {
        this.solrClient = solr;
    }

    @POST
    @Path("/API/{title}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response editWiki(@PathParam("title") String urlTitle,
                             @FormDataParam("data") String data) throws IOException, URISyntaxException, SolrServerException {

        Wiki wiki = wikiFromString(data);
        String sanitizedUrlTitle = sanitizeTitle(urlTitle);
        Wiki found = accessor.read(sanitizedUrlTitle);
        if (shouldMove(wiki, sanitizedUrlTitle, found)) {
            return Response.temporaryRedirect(new URI(move(found, wiki).getRedirect())).build();
        } else if(shouldCreate(wiki, sanitizedUrlTitle, found)) {

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", wiki.getTitle());
            doc.addField("wiki", wiki.getBody());
            doc.addField("title", wiki.getTitle());

            solrClient.add(doc);
            accessor.create(wiki.getTitle(), wiki);

            WikiResponse createdResponse = new WikiResponse(wiki, "CREATED");
            return Response.ok(createdResponse).build();
        } else if (shouldUpdate(wiki, sanitizedUrlTitle, found)) {
            found.setBody(wiki.getBody());
            accessor.set(found.getTitle(), found);
            WikiResponse updatedResposne = new WikiResponse(found, "UPDATED");
            return Response.ok(updatedResposne).build();
        } else
            return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/API/{title}")
    @Timed
    public Wiki getWiki(@PathParam("title") String title) {
        return accessor.read(sanitizeTitle(title));
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{title}")
    @Timed
    public Response viewWiki(@PathParam("title") String title) throws URISyntaxException {
        String sanitizedTitle = sanitizeTitle(title);
        Wiki wiki = accessor.read(sanitizedTitle);

        if (wiki!= null && wiki.getRedirect()!=null)
            return Response.temporaryRedirect(new URI(wiki.getRedirect())).build();
        if (wiki != null) {
            return Response.ok(new ViewWiki(wiki)).build();
        } else
            return Response.ok(new ViewWiki(new Wiki(sanitizedTitle, ""))).build();
    }



    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{title}/edit")
    @Timed
    public Response editWiki(@PathParam("title") String title) throws URISyntaxException {
        String sanitizedTitle = sanitizeTitle(title);
        Wiki wiki = accessor.read(sanitizedTitle);
        if (wiki != null && wiki.getRedirect()!=null)
            return Response.temporaryRedirect(new URI(wiki.getRedirect())).build();
        if (wiki != null) {
            return Response.ok(new EditWiki(wiki)).build();
        } else
            return Response.ok(new EditWiki(new Wiki(sanitizedTitle, ""))).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{title}/move")
    @Timed
    public Response moveWiki(@PathParam("title") String title) throws URISyntaxException {
        Wiki wiki = accessor.read(sanitizeTitle(title));

        if (wiki != null && wiki.getRedirect()!=null)
            return Response.temporaryRedirect(new URI(wiki.getRedirect())).build();
        if (wiki != null) {
            return Response.ok(new MoveWiki(wiki)).build();
        } else
            return Response.ok(new ViewWiki(new Wiki(title, ""))).build();
    }


    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/search")
    @Timed
    public Response search(@QueryParam("q") String q) throws URISyntaxException, IOException, SolrServerException {
        SolrQuery query = new SolrQuery();
        logger.info("Query: " + q);
        query.setQuery("wiki:"+q+" OR title:"+q);

        query.setHighlight(true).setHighlightSnippets(1); //set other params as needed
        query.setHighlightSimplePre("");
        query.setHighlightSimplePost("");
        query.setParam("hl.fl", "wiki");
        QueryResponse queryResponse = solrClient.query(query);
        Iterator<SolrDocument> iter = queryResponse.getResults().iterator();

        ArrayList results = new ArrayList<Map<String, String>>();
        while (iter.hasNext()) {
            SolrDocument resultDoc = iter.next();
            String id = (String) resultDoc.getFieldValue("id");
            if (queryResponse.getHighlighting().get(id) != null) {
                HashMap<String, String> resultMap = new HashMap<String, String>();
                resultMap.put("title", id);
                resultMap.put("highlight", WikiModel.toHtml(queryResponse.getHighlighting().get(id).get("wiki").get(0)));
                results.add(resultMap);
            }
        }
        logger.info(results.toString());
        return Response.ok(new SearchResults(q, results)).build();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/grading/{title}")
    @Timed
    public Response getGrading(@PathParam("title") String title) throws URISyntaxException {
        return Response.temporaryRedirect(new URI(title)).build();
    }

    private boolean shouldMove(Wiki wiki, String sanitizedUrlTitle, Wiki found) {
        return !sanitizedUrlTitle.equals(sanitizeTitle(wiki.getTitle())) && found != null;
    }

    private boolean shouldCreate(Wiki wiki, String sanitizedUrlTitle, Wiki found) {
        return sanitizedUrlTitle.equals(sanitizeTitle(wiki.getTitle())) && found == null;
    }

    private boolean shouldUpdate(Wiki wiki, String sanitizedUrlTitle, Wiki found) {
        return sanitizedUrlTitle.equals(wiki.getTitle()) && found != null;
    }

    private Wiki move(Wiki source, Wiki destination) {
        accessor.set(destination.getTitle(), destination);
        source.setBody("");
        source.setRedirect(destination.getTitle());
        accessor.set(source.getTitle(), source);
        return source;
    }

    private Wiki wikiFromString(String data) throws IOException {
        return mapper.readValue(data, Wiki.class);
    }



    private String sanitizeTitle(String title) {
        return title.replace('_', ' ');
    }
}