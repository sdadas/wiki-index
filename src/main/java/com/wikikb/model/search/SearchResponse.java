package com.wikikb.model.search;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse<T extends SearchResult> {

    private List<T> results;
    private long total;

    public static <T extends SearchResult> SearchResponse<T> empty() {
        return new SearchResponse<>();
    }

    public SearchResponse() {
        this.results = new ArrayList<>();
        this.total = 0L;
    }

    public SearchResponse(List<T> results, long total) {
        this.results = results;
        this.total = total;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
