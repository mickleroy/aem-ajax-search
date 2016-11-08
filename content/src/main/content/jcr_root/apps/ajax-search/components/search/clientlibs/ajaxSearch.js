/**
* This component performs an AJAX search of the content within the JCR and 
* displays the results using Handlebars.
*/
(function($) {
    var $comp        = $('.ajax-search'),
        $form        = $('form', $comp),
        $searchField = $('input[name="searchTerm"]', $form),
        $results     = $('.ajax-search_results'),
        startIndex   = 0,
        hitsPerPage,
        resultTmpl;

    /* Component configuration */
    var config = {
        endpoint: $form.data('url'),
        pagesize: $form.data('page-size')
    }

    /**
    * Performs initialisation steps for this component.
    */
    function init() {
        resultTmpl = Handlebars.compile($('#search-results--tmpl').html());
    }
    
    /**
    * Performs an AJAX search against a predefined endpoint using the foundation
    * search query parameters.
    */
    function performSearch() {
        var searchParams = {
            "q": $searchField.val(),
            "start": startIndex
        };
        
        // optional override of page size
        if(config.pagesize) {
            searchParams.pageSize = config.pagesize;
        }

        $.get(config.endpoint, searchParams)
        .success(function(data) {
            hitsPerPage = data.hitsPerPage;
            data.hasNext = data.totalHits > (startIndex + hitsPerPage);
            data.hasPrev = (startIndex - hitsPerPage) >= 0;
            $results.html(resultTmpl(data));
        })
        .error(function() {
            console.log('[ERROR] Could not perform search');
        });
    }

    /**
    * On submit, reset index and trigger a search of the content.
    */
    $form.on('submit', function(evt) {
        evt.preventDefault();
        startIndex = 0;
        performSearch();
    });
    
    /**
    * On click of "next" button, display the next result page.
    */
    $results.on('click', '.search-results_next', function(evt) {
        startIndex += hitsPerPage;
        performSearch();
    });
    
    /**
    * On click of "previous" button, display the previous result page.
    */
    $results.on('click', '.search-results_prev', function(evt) {
        startIndex -= hitsPerPage;
        performSearch();
    });
    
    init();
    
})(jQuery);