package org.recap.controller.swagger;

import io.swagger.annotations.*;
import org.recap.ReCAPConstants;
import org.recap.model.SearchRecordsRequest;
import org.recap.model.SearchRecordsResponse;
import org.recap.model.SearchResultRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhish on 13/10/16.
 */
@RestController
@RequestMapping("/searchService")
@Api(value="search", description="Search Records", position = 1)
public class SearchRecordsRestController {

    private Logger logger = LoggerFactory.getLogger(SearchRecordsRestController.class);

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClient;

    public String getServerProtocol() {
        return serverProtocol;
    }

    public void setServerProtocol(String serverProtocol) {
        this.serverProtocol = serverProtocol;
    }

    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public String getScsbSolrClientUrl() {
        return scsbSolrClient;
    }

    public void setScsbSolrClientUrl(String scsbSolrClientUrl) {
        this.scsbSolrClient = scsbSolrClientUrl;
    }

    @RequestMapping(value="/search", method = RequestMethod.POST)
    //@ApiOperation(value = "search",notes = "Search Books in ReCAP - Using Method Post, Request data is String", nickname = "search", position = 0, consumes="application/json")
    //@ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public SearchRecordsResponse searchRecordsServiceGetParam(@RequestBody SearchRecordsRequest searchRecordsRequest) {
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<SearchRecordsRequest> httpEntity = new HttpEntity<>(searchRecordsRequest, getHttpHeaders());

            ResponseEntity<SearchRecordsResponse> responseEntity = getRestTemplate().exchange(getServerProtocol() + getScsbSolrClientUrl() + ReCAPConstants.URL_SEARCH_BY_JSON, HttpMethod.POST, httpEntity, SearchRecordsResponse.class);
            searchRecordsResponse = responseEntity.getBody();
        } catch (Exception e) {
            logger.error("error--."+e);
            logger.error(e.getMessage());
            searchRecordsResponse.setErrorMessage(e.getMessage());
        }
        return searchRecordsResponse;
    }

    @RequestMapping(value="/searchByParam", method = RequestMethod.GET)
    @ApiOperation(value = "searchParam",notes = "Search Books in ReCAP - Using Method GET, Request data as parameter", nickname = "search", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public List<SearchResultRow> searchRecordsServiceGet(
            @RequestParam(name="fieldValue", required = false)  String fieldValue,
            @ApiParam(name="fieldName",required = false,allowableValues = "Author_search,Title_search,TitleStartsWith,Publisher,PublicationPlace,PublicationDate,Subject,ISBN,ISSN,OCLCNumber,Notes,CallNumber_search,Barcode") @RequestParam(name="fieldName", value = "fieldName" , required = false)  String fieldName,
            @ApiParam(name="owningInstitutions", value= "Owning Institutions : PUL, CUL, NYPL")@RequestParam(name="owningInstitutions",required = false ) String owningInstitutions,
            @ApiParam(name="collectionGroupDesignations", value = "collection Designations : Shared,Private,Open") @RequestParam(name="collectionGroupDesignations", value = "collectionGroupDesignations" , required = false)  String collectionGroupDesignations,
            @ApiParam(name="availability", value = "Availability: Available, NotAvailable") @RequestParam(name="availability", value = "availability" , required = false)  String availability,
            @ApiParam(name="materialTypes", value = "MaterialTypes: Monograph, Serial, Other") @RequestParam(name="materialTypes", value = "materialTypes" , required = false)  String materialTypes,
            @ApiParam(name="useRestrictions", value = "Use Restrictions: NoRestrictions, InLibraryUse, SupervisedUse") @RequestParam(name="useRestrictions", value = "useRestrictions" , required = false)  String useRestrictions,
            @ApiParam(name="pageSize", value = "Page Size in Numers - 10, 20 30...") @RequestParam(name="pageSize", required = false) Integer pageSize
    ) {
        HttpEntity<List> responseEntity = null;
        HttpEntity request = new HttpEntity(getHttpHeaders());
        RestTemplate restTemplate = new RestTemplate();
        List<SearchResultRow> searchResultRows = null;
        try {

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getServerProtocol() + getScsbSolrClientUrl() + ReCAPConstants.URL_SEARCH_BY_PARAM)
                    .queryParam("fieldValue", fieldValue)
                    .queryParam("fieldName", fieldName)
                    .queryParam("owningInstitutions", owningInstitutions)
                    .queryParam("collectionGroupDesignations", collectionGroupDesignations)
                    .queryParam("availability", availability)
                    .queryParam("materialTypes", materialTypes)
                    .queryParam("useRestrictions", useRestrictions)
                    .queryParam("pageSize", pageSize);

            responseEntity = getRestTemplate().exchange(builder.build().encode().toUri(), HttpMethod.GET, request, List.class);
            searchResultRows = responseEntity.getBody();
        } catch (Exception e) {
            searchResultRows = new ArrayList<>();
            logger.error("Exception"+e);
        }
        return searchResultRows;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(ReCAPConstants.API_KEY, ReCAPConstants.RECAP);
        return headers;
    }
}
