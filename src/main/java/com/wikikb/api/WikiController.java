package com.wikikb.api;

import com.wikikb.model.WikiPage;
import com.wikikb.model.search.SearchRequest;
import com.wikikb.model.search.SearchResponse;
import com.wikikb.search.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ForkJoinPool;

@RestController
@CrossOrigin("*")
public class WikiController {

    private final SearchService search;

    public WikiController(SearchService search) {
        this.search = search;
    }

    @PostMapping("/{code}/search")
    public SearchResponse<WikiPage> search(@PathVariable String code, @RequestBody SearchRequest request) {
        return search.query(code, request);
    }

    @PostMapping("/{code}/add")
    public void add(@PathVariable String code, @RequestBody WikiPage[] batch) {
        ForkJoinPool.commonPool().execute(() -> search.add(code, batch));
    }
}
