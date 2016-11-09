
package com.github.mickleroy.servlets;

import com.day.cq.wcm.foundation.Search;
import java.io.PrintWriter;
import junit.framework.Assert;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class GeoSiteSearchServletTest {

    protected GeoSiteSearchServlet servlet;
    
    @Mock
    protected SlingHttpServletRequest mockRequest;
    @Mock
    protected SlingHttpServletResponse mockResponse;
    @Mock
    protected Resource resource;
    @Mock
    protected ResourceResolver resourceResolver;
    @Mock
    protected PrintWriter printWriter;
        
    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        
        when(mockRequest.getParameter(anyString())).thenReturn(null);
        when(mockRequest.getResource()).thenReturn(resource);
        when(mockRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(mockResponse.getWriter()).thenReturn(printWriter);
        
        this.servlet = spy(new GeoSiteSearchServlet());
    }
    
    @Test
    public void testSearchPath() {
        Assert.assertEquals("/content/geometrixx-outdoors/en", servlet.getSearchPath());
    }
    
    @Test
    public void testDefaultPageSize() {
        Assert.assertEquals(10l, servlet.getDefaultPageSize());
    }
    
    @Ignore
    @Test
    public void testGetResult() throws Exception {
        // Cannot get complete coverage for two reasons: 
        // 1) cannot mock Search as it is final
        // 2) bytecode error in Search constructor
        Search search = new Search(mockRequest);
        
        when(servlet.getSearch(mockRequest)).thenReturn(search);
        
        servlet.doGet(mockRequest, mockResponse);
        
        verify(mockRequest,  times(1)).getParameter("pageSize");
        verify(mockResponse, times(1)).setContentType("application/json");
        verify(mockResponse, times(1)).getWriter();
    }
}
