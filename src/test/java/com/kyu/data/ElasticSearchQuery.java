package com.kyu.data;

import com.kyu.data.elasticsearch.model.Friend;
import com.kyu.data.elasticsearch.model.Phone;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchQuery {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Before
    public void before() {
        esTemplate.deleteIndex(Phone.class);
        esTemplate.createIndex(Phone.class);
        esTemplate.putMapping(Phone.class);
        esTemplate.refresh(Phone.class);

        IndexQuery phone1 = new Phone(1, "01011111111", "nklee", new DateTime(2017, 7, 15, 0, 0).toDate()).addFriend(new Friend("원빈", 33)).addFriend(new Friend("장동건", 34)).buildIndex();
        IndexQuery phone2 = new Phone(2, "01022222222", "nklee2", new DateTime(2017, 11, 15, 0, 0).toDate()).buildIndex();
        IndexQuery phone3 = new Phone(3, "01033333333", "nklee", new DateTime(2017, 10, 19, 0, 0).toDate()).addFriend(new Friend("원빈1", 33)).addFriend(new Friend("장동건1", 34)).buildIndex();
        IndexQuery phone4 = new Phone(4, "01044444444", "nklee4", new DateTime(2017, 10, 14, 0, 0).toDate()).buildIndex();
        IndexQuery phone5 = new Phone(5, "01055555555", "nklee", new DateTime(2017, 10, 14, 0, 0).toDate()).buildIndex();
        IndexQuery phone6 = new Phone(6, "01066666666", "nklee6", new DateTime(2017, 11, 15, 0, 0).toDate()).buildIndex();
        IndexQuery phone7 = new Phone(7, "01077777777", "nklee", new DateTime(2017, 6, 15, 0, 0).toDate()).addFriend(new Friend("원빈2", 33)).addFriend(new Friend("장동건2", 34)).buildIndex();
        IndexQuery phone8 = new Phone(8, "01088888888", "nklee8", new DateTime(2017, 11, 15, 0, 0).toDate()).buildIndex();
        IndexQuery phone9 = new Phone(9, "01099999999", "nklee", new DateTime(2017, 7, 15, 0, 0).toDate()).buildIndex();
        IndexQuery phone10 = new Phone(10, "10", "nklee10", new DateTime(2017, 11, 15, 0, 0).toDate()).buildIndex();
        IndexQuery phone11 = new Phone(11, "11", "nklee11", new DateTime(2017, 11, 15, 0, 0).toDate()).buildIndex();

        List<IndexQuery> indexQueries = new ArrayList<>();

        // indexing documents
        indexQueries.add(phone1);
        indexQueries.add(phone2);
        indexQueries.add(phone3);
        indexQueries.add(phone4);
        indexQueries.add(phone5);
        indexQueries.add(phone6);
        indexQueries.add(phone7);
        indexQueries.add(phone8);
        indexQueries.add(phone9);
        indexQueries.add(phone10);
        indexQueries.add(phone11);

        // bulk index
        esTemplate.bulkIndex(indexQueries);
        esTemplate.refresh(Phone.class);
    }

    @Test
    public void 테스트_GetQuery() {
        GetQuery getQuery = new GetQuery();
        getQuery.setId("3"); // 한 개의 Document 가져오기 위한 id

        Phone phone = esTemplate.queryForObject(getQuery, Phone.class);
        assertThat("01033333333", is(phone.getNumber()));
    }

    @Test
    public void 테스트_nested() throws InterruptedException {
        BoolQueryBuilder builder = boolQuery();
        builder.must(nestedQuery("friends", termQuery("friends.name", "원빈"))); // 리스트 안의 object 속성으로 조회

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .build();

        Page<Phone> page = esTemplate.queryForPage(searchQuery, Phone.class);
        assertThat(page, Is.is(notNullValue()));
        assertThat(1L, is(page.getTotalElements()));
        assertThat("nklee", is(page.getContent().get(0).getAuthor()));
    }

    @Test
    public void 테스트_bool_query_must() throws InterruptedException {
        BoolQueryBuilder builder = boolQuery();
        builder.must(queryStringQuery("01033333333").field("number"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .build();

        Page<Phone> phones = esTemplate.queryForPage(searchQuery, Phone.class);
        assertThat(1L, is(phones.getTotalElements()));
    }

    @Test
    public void 테스트_날짜검색() {
        BoolQueryBuilder builder = boolQuery()
                .must(queryStringQuery("nklee").field("author"))
                .filter(rangeQuery("dateUp").gte("now-1d").lt("now"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .build();

        Page<Phone> phones = esTemplate.queryForPage(searchQuery, Phone.class);
        System.out.println("------------------------------");
        for (Phone phone : phones) {
            System.out.println(phone);
        }
        System.out.println("------------------------------");
    }

    @Test
    public void 테스트_Aggregation() {
        BoolQueryBuilder builder = boolQuery()
                .must(queryStringQuery("nklee").field("author"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(builder)
                .addAggregation(AggregationBuilders.terms("authors").field("author").size(100))
                .build();

        Aggregations aggregations = esTemplate.query(searchQuery, (res) -> res.getAggregations());
        Terms fieldATerms = aggregations.get("authors");

        for (Terms.Bucket filedABucket : fieldATerms.getBuckets()) {
            Object obj = filedABucket.getKey();
            assertThat("nklee", is(obj));

            long fieldACount = filedABucket.getDocCount();
            assertThat(5L, is(fieldACount));
        }
    }

}


