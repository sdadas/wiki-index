package com.wikikb.search.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wikikb.model.WikiPage;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class WikiPageDocConverter {

    private final ObjectMapper mapper;

    public WikiPageDocConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Document toDocument(WikiPage page) {
        Document doc = new Document();
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
