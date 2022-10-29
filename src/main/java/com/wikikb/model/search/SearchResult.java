package com.wikikb.model.search;

import java.io.Serializable;

public class SearchResult implements Serializable {

    private Integer docId;
    private Double score;

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
