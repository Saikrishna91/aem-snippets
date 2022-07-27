package com.eurail.aem.core.imdb;

import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Servlet.class,
property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.resourceTypes=eurail/configuration/submission/searchMovieTrailer"
})
@ServiceDescription("IMDBSearchServlet")
public class IMDBSearchServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(IMDBSearchServlet.class);
	
	@Reference
	private transient IMDBSearchService imdbSearchService;
	
	/* Two Search Options are available in the AEM Page 
	 * requestParameter 1 - isNormalSearch: Should be sent as true for normalSearch Flow and false for AdvancedSearch Flow.
	 * requestParameter 2 - findIMDBID: Should be sent as true to get IMDBId for both normal and advancedSearch Flow.
	 * requestParameter 3 - searchKeyword: Contains a single searchKeyword like "KungFu Panda" or as queryParameter string. 
	 * requestParameter 4 - imdbVideoID: IMDBID should be passed to get the Video URL from YouTube.
	 * */
	
	@Override
    public void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
		String normalSearch = request.getParameter(Constants.NORMAL_SEARCH);
		String findIMDBId = request.getParameter(Constants.FIND_IMDB_ID);
		String searchKeyword = request.getParameter(Constants.SEARCH_KEYWORD);
		String imdbVideoID = request.getParameter(Constants.IMDB_VIDEO_ID);
		if(normalSearch == null || findIMDBId == null || searchKeyword == null) {
			LOG.error("Incoming request is either empty or does not contain valid request parameters.");
			response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
			response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, "Incoming request is either empty or does not contain valid request parameters.");
		}
		boolean isnormalSearch = Boolean.parseBoolean(normalSearch);
		boolean isFindIMDBId = Boolean.parseBoolean(findIMDBId);
		String cachedResult = imdbSearchService.getCachedResultsFromFile(imdbVideoID, searchKeyword, request.getResourceResolver());
		String apiResponse = cachedResult.isEmpty() ? imdbSearchService.getIMDBAPIConnection(request.getResourceResolver(), isnormalSearch, isFindIMDBId, searchKeyword, imdbVideoID) : cachedResult;
		if(!apiResponse.isEmpty()) {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setStatus(SC_OK);
			response.getWriter().write(apiResponse);
		}
	}

	/* Getter Method for JUnit Test case */
	public IMDBSearchService getImdbSearchService() {
		return imdbSearchService;
	}

	/* Setter Method for JUnit Test case */
	public void setImdbSearchService(IMDBSearchService imdbSearchService) {
		this.imdbSearchService = imdbSearchService;
	}
	
}
