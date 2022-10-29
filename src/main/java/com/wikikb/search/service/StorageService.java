package com.wikikb.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wikikb.search.lang.Language;
import com.wikikb.search.lang.StandardLangauge;
import com.wikikb.search.StorageConfig;
import com.wikikb.search.index.SearchIndex;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class StorageService {

    private final static Logger LOG = LoggerFactory.getLogger(StorageService.class);
    private final StorageConfig config;
    private final Map<String, SearchIndex> wikis;
    private final ObjectMapper mapper;

    public StorageService(StorageConfig config, ObjectMapper mapper) throws IOException {
        this.mapper = mapper;
        this.config = config;
        this.wikis = new HashMap<>();
        this.createDir(new File(this.config.getDir()));
        this.initWikis();
    }

    public SearchIndex getWiki(String code) {
        SearchIndex index = wikis.get(code);
        if (index == null) {
            index = createWiki(code);
            wikis.put(index.lang().code(), index);
        }
        return index;
    }

    public synchronized SearchIndex createWiki(String code) {
        File dir = new File(config.getDir(), code);
        return initWiki(dir);
    }

    private void createDir(File dir) throws IOException {
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        } else if (dir.exists() && !dir.isDirectory()) {
            throw new IllegalStateException(dir.getName() + " - path exists and it's not a directory!");
        }
    }

    private void initWikis() {
        File dir = new File(this.config.getDir());
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.isDirectory()) continue;
            SearchIndex index = initWiki(file);
            wikis.put(index.lang().code(), index);
        }
    }

    private SearchIndex initWiki(File dir) {
        try {
            String code = dir.getName();
            Language language = StandardLangauge.getByCode(code);
            this.createDir(dir);
            return new SearchIndex(dir, language, mapper);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @PreDestroy
    private void close() {
        for (SearchIndex index : wikis.values()) {
            LOG.info("Closing wiki {}", index.lang().code());
            index.close();
        }
    }
}
