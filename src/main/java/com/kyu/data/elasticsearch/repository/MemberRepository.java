package com.kyu.data.elasticsearch.repository;

import com.kyu.data.elasticsearch.model.Member;
import com.kyu.data.elasticsearch.model.Phone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

import java.util.List;


public interface MemberRepository extends ElasticsearchCrudRepository<Member, Integer> {

}
