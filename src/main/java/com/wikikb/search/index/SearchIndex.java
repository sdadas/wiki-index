package com.wikikb.search.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wikikb.search.lang.Language;
import com.wikikb.model.WikiPage;
import com.wikikb.model.search.SearchResponse;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.NIOFSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchIndex implements AutoCloseable {

    private final static Logger LOG = LoggerFactory.getLogger(SearchIndex.class);
    private final File indexDir;
    private final Language lang;
    private final Analyzer analyzer;
    private final BM25Similarity similarity;
    private final IndexWriter writer;
    private final SearcherManager manager;
    private final WikiPageDocConverter converter;

    public SearchIndex(File indexDir, Language lang, ObjectMapper mapper) {
        this.indexDir = indexDir;
        this.lang = lang;
        this.analyzer = lang.analyzer();
        this.similarity = new BM25Similarity();
        this.writer = createIndexWriter();
        this.manager = createSearcherManager(writer);
        this.converter = new WikiPageDocConverter(mapper);
    }

    private IndexWriter createIndexWriter() {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setSimilarity(similarity);
        try {
            return new IndexWriter(NIOFSDirectory.open(indexDir.toPath()), config);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private SearcherManager createSearcherManager(IndexWriter writer) {
        try {
            return new SearcherManager(writer, true, true, new SearcherFactory());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void add(WikiPage... batch) {
        try {
            List<Document> docs = Arrays.stream(batch).map(converter::toDocument).collect(Collectors.toList());
            writer.addDocuments(docs);
            writer.commit();
        } catch (Exception e) {
            LOG.error("Failed to commit batch", e);
            throw new IllegalStateException(e);
        }
    }

    public SearchResponse<WikiPage> query(Query query, Sort sort, int page, int pageSize) {
        IndexSearcher searcher = null;
        try {
            manager.maybeRefreshBlocking();
            searcher = manager.acquire();
            searcher.setSimilarity(similarity);

            int offset = page * pageSize;
            int size = Math.max(offset + pageSize, 1);
            TopDocs docs;
            if (sort != null)
                docs = searcher.search(query, size, sort);
            else
                docs = searcher.search(query, size);

            List<WikiPage> results = new ArrayList<>();
            for (int i = 0; i < docs.scoreDocs.length; i++) {
                if (i < offset) continue;
                ScoreDoc scoreDoc = docs.scoreDocs[i];
                Document doc = searcher.doc(scoreDoc.doc);
                results.add(converter.fromDocument(doc, scoreDoc.doc, scoreDoc.score));
            }
            return new SearchResponse<>(results, docs.totalHits.value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            if (searcher != null) {
                try {
                    manager.release(searcher);
                } catch (IOException ex) {
                    /* DO NOTHING */
                }
            }
        }
    }

    public Language lang() {
        return this.lang;
    }

    public Analyzer analyzer() {
        return this.analyzer;
    }

    @Override
    public void close() {
        try {
            writer.close();
            manager.close();
        } catch (IOException e) {
            /* DO NOTHING */
        }
    }
}
