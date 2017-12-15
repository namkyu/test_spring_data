package com.kyu.data.elasticsearch.core;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @Project : spring_data
 * @Date : 2017-12-14
 * @Author : nklee
 * @Description :
 */
@Component("esName")
public class EsIndexName {

    @Getter @Setter
    private String month;

    @PostConstruct
    public void init() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String formattedString = localDate.format(formatter);
        this.month = formattedString;
    }

    public String[] getIndices(int minusMonth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        LocalDate localDate = LocalDate.now();
        String[] indices = new String[minusMonth];
        for (int i = 0; i < minusMonth; i++) {
            String formattedString = localDate.minusMonths(i).format(formatter);
            indices[i] = formattedString;
        }

        return indices;
    }

    public String getMonth() {
        System.out.println("month : " + month);
        return month;
    }
}
