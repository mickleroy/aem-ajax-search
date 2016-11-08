package com.github.mickleroy.servlets;

import com.day.cq.wcm.foundation.Search;
import javax.jcr.RepositoryException;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;

/**
 * This servlet is used to search pages within the Geometrixx Outdoors site.
 */
@SlingServlet(paths = {"/bin/geometrixx/search/site"}, methods = {"GET"}, extensions = {"html"}, metatype = true)
@Properties({
        @Property(name = "service.pid", value = "com.github.mickleroy.servlets.GeoSiteSearchServlet", propertyPrivate = false),
        @Property(name = "service.description", value = "Geometrixx Outdoors Site Search Servlet", propertyPrivate = false)})
public class GeoSiteSearchServlet extends AbstractSearchServlet {

    @Override
    protected String getSearchPath() {
        return "/content/geometrixx-outdoors/en";
    }

    @Override
    protected long getDefaultPageSize() {
        return 10l;
    }

    @Override
    protected void writeJSONHit(Search.Hit hit, JSONWriter jsonWriter) throws JSONException, RepositoryException {
        jsonWriter.object();
        jsonWriter.key("title").value(hit.getTitle());
        jsonWriter.key("url").value(hit.getURL());
        jsonWriter.key("excerpt").value(hit.getExcerpt());
        addPageProperty(hit, jsonWriter, "tags", "cq:tags");
        jsonWriter.endObject();
    }
}
