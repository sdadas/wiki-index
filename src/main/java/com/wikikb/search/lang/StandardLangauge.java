package com.wikikb.search.lang;

import com.wikikb.search.analysis.PolishAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public enum StandardLangauge implements Language {

    ENGLISH("en") {
        @Override
        public Analyzer analyzer() {
            return new EnglishAnalyzer();
        }
    },
    POLISH("pl") {
        @Override
        public Analyzer analyzer() {
            return new PolishAnalyzer();
        }
    };

    private final String code;

    StandardLangauge(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }

    public Analyzer analyzer() {
        return new StandardAnalyzer();
    }

    public static Language getByCode(String code) {
        for (StandardLangauge value : values()) {
            if (StringUtils.equalsIgnoreCase(code, value.code)) {
                return value;
            }
        }
        return new CustomLanguage(code);
    }
}
