package edu.sjsu.cmpe275.model;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
	List<SearchResponse> searchResponse;
	List<SearchResponse> returnResponse;
	public SearchResult(){
		searchResponse = new ArrayList<SearchResponse>();
		returnResponse = new ArrayList<SearchResponse>();
	}
	public List<SearchResponse> getReturnResponse() {
		return returnResponse;
	}
	public void setReturnResponse(List<SearchResponse> returnResponse) {
		this.returnResponse = returnResponse;
	}
	public List<SearchResponse> getSearchResponse() {
		return searchResponse;
	}

	public void setSearchResponse(List<SearchResponse> searchResponse) {
		this.searchResponse = searchResponse;
	}
	
	
}