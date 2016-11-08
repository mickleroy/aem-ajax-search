
package com.github.mickleroy.servlets

import spock.lang.Specification

class GeoSiteSearchServletSpec extends Specification {
    
    GeoSiteSearchServlet servlet
    
    def setup() {
        servlet = new GeoSiteSearchServlet()
    }
    
    def "should return correct search path"() {
        given:
            def path = null
        when:
            path = servlet.getSearchPath()
        then:
            path == "/content/geometrixx-outdoors/en"
    }
    
    def "should return correct default page size"() {
        given:
            def size = null
        when:
            size = servlet.getDefaultPageSize()
        then:
            size == 10
    }
}

