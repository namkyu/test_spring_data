package com.kyu.data;

import com.kyu.data.elasticsearch.core.EsIndexName;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @Project : spring_data
 * @Date : 2017-12-14
 * @Author : nklee
 * @Description :
 */
public class DateTest {

    @Test
    public void 날짜() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.parse("2017/06/10", formatter);
        System.out.println(String.format("%02d", localDate.getMonthValue()));
    }

    @Test
    public void test() {
        LocalDate now = LocalDate.now();
        System.out.println(now.getYear());
        System.out.println(now.getMonthValue());
    }

    @Test
    public void test2() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String formattedString = localDate.format(formatter);
        System.out.println(formattedString);
    }

    @Test
    public void test_전월구하기() {
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate.minusMonths(0).getMonthValue());
        System.out.println(localDate.minusMonths(1).getMonthValue());
        System.out.println(localDate.minusMonths(2).getMonthValue());
        System.out.println(localDate.minusMonths(3).getMonthValue());
    }

    @Test
    public void test1() {
        String[] indices = new EsIndexName().getIndices(6);
        System.out.println(Arrays.toString(indices));
    }
}
