package com.wikikb.search.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wikikb.model.WikiPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiPageDocConverter {

    private static final Logger LOG = LoggerFactory.getLogger(WikiPageDocConverter.class);
    private final ObjectMapper mapper;

    public WikiPageDocConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Document toDocument(WikiPage page) {
        Document doc = new Document();
        boolean incorrect = StringUtils.isAnyBlank(page.getTitle(), page.getText(), page.getSummary());
        if (incorrect) {
            LOG.warn("Skipping incorrect document {}", page.getTitle());
            return null;
        }
        doc.add(new StringField("title", page.getTitle(), Field.Store.YES));
        doc.add(new TextField("title_text", page.getTitle(), Field.Store.YES));
        doc.add(new TextField("text", page.getText(), Field.Store.YES));
        doc.add(new TextField("summary", page.getSummary(), Field.Store.YES));
        return doc;
    }

    public WikiPage fromDocument(Document doc, int docId, float score) {
        WikiPage page = new WikiPage();
        page.setTitle(doc.get("title"));
        page.setSummary(doc.get("summary"));
        page.setText(doc.get("text"));
        page.setDocId(docId);
        page.setScore((double) score);
        return page;
    }
}
