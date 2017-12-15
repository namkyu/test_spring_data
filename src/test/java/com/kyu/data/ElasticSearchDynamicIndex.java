package com.kyu.data;

import com.kyu.data.elasticsearch.core.EsIndexName;
import com.kyu.data.elasticsearch.model.Member;
import com.kyu.data.elasticsearch.model.Phone;
import com.kyu.data.elasticsearch.repository.MemberRepository;
import com.kyu.data.elasticsearch.repository.PhoneRepository;
import com.kyu.data.elasticsearch.service.PhoneService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchDynamicIndex {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private EsIndexName esIndexName;

    @Before
    public void before() {
        // default index
        reMakeIndex();

        // index 1
        esIndexName.setMonth("201708");
        reMakeIndex();
        esTemplate.index(new Member(1, "nklee1", new DateTime(2017, 8, 15, 0, 0).toDate()).buildIndex());
        esTemplate.refresh(Member.class);

        // index 2
        esIndexName.setMonth("201709");
        reMakeIndex();
        esTemplate.index(new Member(2, "nklee2", new DateTime(2017, 9, 15, 0, 0).toDate()).buildIndex());
        esTemplate.index(new Member(3, "nklee3", new DateTime(2017, 9, 15, 0, 0).toDate()).buildIndex());
        esTemplate.index(new Member(4, "nklee4", new DateTime(2017, 9, 15, 0, 0).toDate()).buildIndex());
        esTemplate.refresh(Member.class);

        // index 3
        esIndexName.setMonth("201710");
        reMakeIndex();
        esTemplate.index(new Member(5, "nklee5", new DateTime(2017, 10, 15, 0, 0).toDate()).buildIndex());
        esTemplate.refresh(Member.class);
    }

    private void reMakeIndex() {
        esTemplate.deleteIndex(Member.class);
        esTemplate.createIndex(Member.class);
        esTemplate.putMapping(Member.class);
    }

    @Test
    public void 테스트_index_monthly() {
        BoolQueryBuilder builder = boolQuery()
                .must(termQuery("name", "nklee3"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("nklee_201710", "nklee_201708", "nklee_201709")
                .withTypes("member")
                .withQuery(builder)
                .build();

        Page<Member> members = esTemplate.queryForPage(searchQuery, Member.class);
        assertThat(1L, is(members.getTotalElements()));
    }

    @Test
    public void 날짜검색() {
        BoolQueryBuilder builder = boolQuery()
                .must(termQuery("name", "nklee5"))
                .must(rangeQuery("create").gte("now-100d").lt("now"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("nklee_201710", "nklee_201708", "nklee_201709")
                .withTypes("member")
                .withQuery(constantScoreQuery(builder))
                .build();

        Page<Member> members = esTemplate.queryForPage(searchQuery, Member.class);
        members.forEach(System.out::println);
    }

}


