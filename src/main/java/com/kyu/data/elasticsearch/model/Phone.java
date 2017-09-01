package com.kyu.data.elasticsearch.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

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
    private String number;
    private String author;

}
