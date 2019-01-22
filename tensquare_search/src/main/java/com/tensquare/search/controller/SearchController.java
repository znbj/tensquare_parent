package com.tensquare.search.controller;

import com.tensquare.search.service.SearchService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @RequestMapping(value = "/search/{keyword}/{page}/{size}", method = RequestMethod.GET)
    public Result search(@PathVariable String keyword, @PathVariable int page, @PathVariable int size) {
        PageResult result = searchService.search(keyword, page, size);
        return new Result(true, StatusCode.OK, "查询成功", result);
    }
}
