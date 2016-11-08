<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

<script id="search-results--tmpl" type="text/x-handlebars-template">
    <p>Found {{totalHits}} matches ({{executionTime}} seconds)</p>
    {{#each hits}}
        <div class="result-item">
            <h4 class="result-item_title">{{title}}</h4>
            <a class="result-item_url" href="{{url}}">{{url}}</a>
            <p class="result-item_excertp">{{{excerpt}}}</p>
            <hr/>
        </div>
    {{/each}}
    {{#if hasPrev}}
        <button class="search-results_prev">Previous</button>
    {{/if}}
    {{#if hasNext}}
        <button class="search-results_next">Next</button>
    {{/if}}
</script>
    
<div class="ajax-search">
    <form data-url="${properties.endpoint}" data-page-size="${properties.pagesize}">
        <label for="searchTerm">Search</label>
        <input type="text" class="field" name="searchTerm" placeholder="">
        <button type="submit" class="button">Submit</button>
    </form>
    <div class="ajax-search_results"></div>
</div>

<cq:includeClientLib categories="apps.ajaxsearch.comp"/>