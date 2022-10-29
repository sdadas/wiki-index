package com.wikikb.search.service;

import com.wikikb.model.WikiPage;
import com.wikikb.model.search.SearchRequest;
import com.wikikb.model.search.SearchResponse;
import com.wikikb.search.index.SearchIndex;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final StorageService storage;

    public SearchService(StorageService storage) {
        this.storage = storage;
    }

    public SearchResponse<WikiPage> query(String code, SearchRequest request) {
        String query = request.getQuery();
        if (request.getPage() == null) request.setPage(0);
        if (request.getPageSize() == null) request.setPageSize(10);
        if (StringUtils.isBlank(query)) return SearchResponse.empty();
        SearchIndex index = this.storage.getWiki(code);
        try {
            String q = QueryParser.escape(query);
            Query textQuery = new BoostQuery(fieldQuery(index, "text", q), 1.0f);
            Query titleQuery = new BoostQuery(fieldQuery(index, "title_text", q), 5.0f);
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(textQuery, BooleanClause.Occur.MUST);
            builder.add(titleQuery, BooleanClause.Occur.SHOULD);
            return index.query(builder.build(), null, 0, 10);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    private Query fieldQuery(SearchIndex index, String field, String query) throws ParseException {
        QueryParser parser = new QueryParser(field, index.analyzer());
        parser.setDefaultOperator(QueryParser.Operator.AND);
        parser.setAllowLeadingWildcard(false);
        return parser.parse(query);
    }

    public synchronized void add(String code, WikiPage[] batch) {
        SearchIndex index = this.storage.getWiki(code);
        index.add(batch);
    }
}
