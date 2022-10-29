package com.wikikb.search.lang;

import org.apache.lucene.analysis.Analyzer;

public interface Language {

    String code();

    Analyzer analyzer();
}
