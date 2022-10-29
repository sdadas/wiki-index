package com.wikikb.search.lang;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class CustomLanguage implements Language {

    private final String code;

    public CustomLanguage(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public Analyzer analyzer() {
        return new StandardAnalyzer();
    }
}
