package com.kyu.data.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Project : test_project
 * @Date : 2017-08-29
 * @Author : nklee
 * @Description :
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.ToString
@Document(indexName = "nklee_#{esName.getMonth()}", type = "member", shards = 2, replicas = 1)
public class Member {

    @Id
    private int id;

    @Field(type = FieldType.String, index = FieldIndex.not_analyzed)
    private String name;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy.MM.dd hh:mm:ss", index = FieldIndex.not_analyzed)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date create;

    public IndexQuery buildIndex() {
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId(String.valueOf(id));
        indexQuery.setObject(this);
        return indexQuery;
    }
}
