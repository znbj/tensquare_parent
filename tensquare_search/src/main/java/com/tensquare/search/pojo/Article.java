package com.tensquare.search.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "tensquare", type = "article")
public class Article {
    @Id
    @Field(type = FieldType.text, store = true)
    private String id;
    @Field(type = FieldType.text, store = true, analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.text, store = true, analyzer = "ik_max_word")
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
