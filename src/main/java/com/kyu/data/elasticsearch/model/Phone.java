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
@Document(indexName = "nklee", type = "phone", shards = 1, replicas = 0)
public class Phone {

    @Id
    private int id;

    @Field(type = FieldType.String)
    private String number;

    @Field(type = FieldType.String)
    private String author;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy.MM.dd hh:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd hh:mm:ss", timezone = "Asia/Seoul")
    private Date dateUp;

    @Field(type = FieldType.Nested)
    private List<Friend> friends = new ArrayList<>();

    public Phone(int id, String number, String author) {
        this.id = id;
        this.number = number;
        this.author = author;
    }

    public Phone(int id, String number, String author, Date dateUp) {
        this.id = id;
        this.number = number;
        this.author = author;
        this.dateUp = dateUp;
    }

    public Phone addFriend(Friend friend) {
        friends.add(friend);
        return this;
    }

    public IndexQuery buildIndex() {
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setId(String.valueOf(id));
        indexQuery.setObject(this);
        return indexQuery;
    }
}
