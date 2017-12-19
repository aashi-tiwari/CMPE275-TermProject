package edu.sjsu.cmpe275.model;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
	List<SearchResponse> searchResponseObjects;
	public SearchResult(){
		searchResponseObjects = new ArrayList<SearchResponse>();
	}
	public List<SearchResponse> getSearchResponse() {
		return searchResponseObjects;
	}

	public void setSearchResponse(List<SearchResponse> searchResponse) {
		this.searchResponseObjects = searchResponse;
	}
	
}
