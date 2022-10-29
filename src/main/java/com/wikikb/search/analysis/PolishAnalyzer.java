package com.wikikb.search.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.charfilter.MappingCharFilter;
import org.apache.lucene.analysis.charfilter.NormalizeCharMap;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.morfologik.MorfologikFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.Reader;
import java.util.Set;

public class PolishAnalyzer extends Analyzer {

    private final Set<String> delimiters = Set.of(".", "_");

    public PolishAnalyzer() {
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new StandardTokenizer();
        MorfologikFilter stemmer = new MorfologikFilter(tokenizer);
        ASCIIFoldingFilter src = new ASCIIFoldingFilter(stemmer, false);
        LowerCaseFilter lowerCase = new LowerCaseFilter(src);
        return new TokenStreamComponents(tokenizer, lowerCase);
    }

    @Override
    protected Reader initReader(String fieldName, Reader reader) {
        NormalizeCharMap.Builder builder = new NormalizeCharMap.Builder();
        delimiters.forEach(delimiter -> builder.add(delimiter, " "));
        MappingCharFilter mappingCharFilter = new MappingCharFilter(builder.build(), reader);
        return new HTMLStripCharFilter(mappingCharFilter);
    }
}
