package com.kyu.data.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

/**
 * @Project : test_project
 * @Date : 2017-08-29
 * @Author : nklee
 * @Description :
 */
@AllArgsConstructor
@Data
public class Friend {

    private String name;

    private int age;

}
