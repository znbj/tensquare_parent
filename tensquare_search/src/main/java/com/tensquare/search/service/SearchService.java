package com.tensquare.search.service;

import com.tensquare.search.dao.ArticleDao;
import com.tensquare.search.pojo.Article;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    @Autowired
    private ArticleDao articleDao;

    public PageResult search(String keyword, int page, int size) {
        Page<Article> result = articleDao.findByTitleOrContent(keyword, keyword, PageRequest.of(page - 1, size));
        return new PageResult(result.getTotalElements(), result.getContent());

    }
}
