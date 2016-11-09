package com.github.mickleroy.servlets;

import com.day.cq.wcm.foundation.Search;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.jcr.RepositoryException;
import org.apache.felix.scr.annotations.Component;
import org.apache.sling.commons.json.JSONArray;

/**
 * This abstract servlet performs a full-text search through the JCR using the foundation Search service.
 * 
 * An implementation must be provided in order to supply:
 * - the servlet path
 * - the search path in the JCR
 * - the default page size
 * - write out the JSON for a hit
 * 
 * The search query uses the foundation component implementation so the following
 * query parameters need to be used:
 * q        - the search query
 * start    - used to navigate the result pages by giving the start index.
 * tag      - constraint results to content that matches given tags
 * mimeType - constraint results to content with specific mime type
 * from/to  - constraint results to content last modified within the given "from" and "to" dates
 * pageSize - defines the page size to use (custom).
 * 
 * A JSON object will be returned, representing the search results (what is contained
 * in the `hits` array is defined by the implementation class).
 */
@Component(componentAbstract = true, inherit = true)
public abstract class AbstractSearchServlet extends SlingSafeMethodsServlet {
    
    private static Logger log = LoggerFactory.getLogger(AbstractSearchServlet.class);
    
    private static final String JSON_MIME_TYPE = "application/json";
    private static final String PARAM_PAGE_SIZE = "pageSize";
    
    /**
     * The path within the JCR to constraint the search to
     * @return
     */
    protected abstract String getSearchPath();
    
    /**
     * The default page size to use. 
     * Note: page size will default to 10 within the base implementation
     * @return 
     */
    protected abstract long getDefaultPageSize();
    
    /**
     * This method must be implemented to write out a hit to the JSON representation
     * of the results.
     * 
     * Example implementation:
     * <code>
     *  jsonWriter.object();
     *  jsonWriter.key("title").value(hit.getTitle());
     *  jsonWriter.endObject();
     * </code>
     * @param hit
     * @param jsonWriter
     * @throws JSONException
     * @throws RepositoryException 
     */
    protected abstract void writeJSONHit(Search.Hit hit, JSONWriter jsonWriter) throws JSONException, RepositoryException;
    

    /**
     * Performs a search through the content using the foundation search service.
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException 
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
                                                    throws IOException, ServletException {
        try {
            long pageSize = getDefaultPageSize();
            if(request.getParameter(PARAM_PAGE_SIZE) != null) {
                pageSize = Long.valueOf(request.getParameter(PARAM_PAGE_SIZE));
            }
            
            Search search = getSearch(request);
            search.setSearchIn(getSearchPath());
            search.setHitsPerPage(pageSize);
            Search.Result result = search.getResult();
            
            log.info("Found " + result.getTotalMatches() + " matches for " + search.getQuery());
            
            StringWriter stringWriter = new StringWriter();
            JSONWriter jsonWriter = new JSONWriter(stringWriter);
            List<Search.Hit> hits = result.getHits();
            
            jsonWriter.object();
            jsonWriter.key("totalHits").value(result.getTotalMatches());
            jsonWriter.key("hitsPerPage").value(search.getHitsPerPage());
            jsonWriter.key("totalPages").value(result.getResultPages().size());
            jsonWriter.key("startIndex").value(result.getStartIndex());
            jsonWriter.key("executionTime").value(result.getExecutionTime());
            
            jsonWriter.key("hits").array();
            for(Search.Hit hit : hits) {
                writeJSONHit(hit, jsonWriter);
            }
            jsonWriter.endArray();
            jsonWriter.endObject();
            
            response.setContentType(JSON_MIME_TYPE);
            response.getWriter().write(stringWriter.toString());
            response.getWriter().flush();
            
        } catch (JSONException e) {
            throw new ServletException("Unable to render json array of search results, error: ", e);
        } catch (RepositoryException ex) {
            throw new ServletException("Unable to access JCR repository, error: ", ex);
        }
    }
    
    /**
     * Utility method to add a page property from a result to the JSON representation of a hit. It handles multi-value properties gracefully
     * Note: the property key will be used as the JSON key for the attribute.
     * @param hit
     * @param jsonWriter
     * @param propertyKey
     * @throws JSONException
     * @throws RepositoryException 
     */
    protected void addPageProperty(Search.Hit hit, JSONWriter jsonWriter, String propertyKey) throws JSONException, RepositoryException {
        addPageProperty(hit, jsonWriter, propertyKey, propertyKey);
    }
    
    /**
     * Utility method to add a page property from a result to the JSON representation of a hit. It handles multi-value properties gracefully.
     * @param hit
     * @param jsonWriter
     * @param jsonKey
     * @param propertyKey
     * @throws JSONException
     * @throws RepositoryException 
     */
    protected void addPageProperty(Search.Hit hit, JSONWriter jsonWriter, String jsonKey, String propertyKey) throws JSONException, RepositoryException {
        Map properties = hit.getProperties();
        
        if(properties.containsKey(propertyKey)) {
            Object propValue = properties.get(propertyKey);
            jsonWriter.key(jsonKey);
            if(propValue instanceof String[]) {
                jsonWriter.value(new JSONArray(Arrays.asList((String[])propValue)));
            } else {
                jsonWriter.value(properties.get(propertyKey));
            }
        }
    }
    
    /**
     * For testing purposes only.
     */
    protected Search getSearch(SlingHttpServletRequest request) {
        return new Search(request);
    } 
}
