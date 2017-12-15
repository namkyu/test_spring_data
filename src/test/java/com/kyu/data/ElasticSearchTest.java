package com.kyu.data;

import com.kyu.data.elasticsearch.model.Phone;
import com.kyu.data.elasticsearch.repository.PhoneRepository;
import com.kyu.data.elasticsearch.service.PhoneService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchTest {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private PhoneRepository phoneRepository;

    @Before
    public void before() {
        esTemplate.deleteIndex(Phone.class);
        esTemplate.createIndex(Phone.class);
        esTemplate.putMapping(Phone.class);
        esTemplate.refresh(Phone.class);
    }

    @Test
    public void 데이터저장() {
        Phone phone = new Phone(2, "010-1111-1111", "nklee", new Date());
        Phone testPhone = phoneService.save(phone);

        System.out.println(testPhone);
        assertNotNull(testPhone.getId());
        assertThat(testPhone.getNumber(), is(phone.getNumber()));
    }

    @Test
    public void 조회() {
        Phone phone = new Phone(1, "010-1111-1111", "nklee");
        phoneService.save(phone);

        Phone testPhone = phoneService.findOne(1);
        assertNotNull(testPhone.getId());
        assertThat(testPhone.getNumber(), is(phone.getNumber()));
    }

    @Test
    public void 조회_핸드폰번호이용() {
        Phone phone = new Phone(1, "010-1111-1111", "nklee");
        Phone phone2 = new Phone(2, "010-1111-1111", "nklee");
        phoneService.save(phone);
        phoneService.save(phone2);

        List<Phone> numbers = phoneService.findByNumber(phone.getNumber());
        assertThat(numbers.size(), is(2));
    }

    @Test
    public void 조회_페이징() {
        List<Phone> phoneList = new ArrayList<>();
        phoneList.add(new Phone(1, "010-1111-1111", "nklee"));
        phoneList.add(new Phone(2, "010-2222-2222", "nklee2"));
        phoneList.add(new Phone(3, "010-3333-3333", "nklee3"));
        phoneList.add(new Phone(4, "010-4444-4444", "nklee4"));
        phoneList.add(new Phone(5, "010-5555-5555", "nklee5"));
        phoneList.add(new Phone(6, "010-6666-6666", "nklee6"));
        phoneList.add(new Phone(7, "010-7777-7777", "nklee7"));
        phoneList.add(new Phone(8, "010-8888-8888", "nklee"));
        phoneList.add(new Phone(9, "010-9999-9999", "nklee"));
        phoneList.add(new Phone(10, "010-1212-1212", "nklee"));
        phoneList.add(new Phone(11, "010-1313-1313", "nklee"));

        // 테스트 데이터 저장
        for (Phone phone : phoneList) {
            phoneService.save(phone);
        }

        // 검증
        Page<Phone> phones = phoneService.findByAuthor("nklee", new PageRequest(0, 10));
        assertThat(phones.getTotalElements(), is(5L));

        Page<Phone> phones2 = phoneService.findByAuthor("nklee3", new PageRequest(0, 10));
        assertThat(phones2.getTotalElements(), is(1L));
    }

    @Test
    public void 삭제() {
        Phone phone = new Phone(1, "010-1111-1111", "nklee");
        phoneService.save(phone);
        phoneService.delete(phone);

        Phone testPhone = phoneService.findOne(1);
        assertNull(testPhone);
    }

    @Test
    public void bulk_insert() {
        List<Phone> phoneList = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            phoneList.add(new Phone(i, "010-1111-1111", "nklee" + i));

            if (i % 1000000 == 0) {
                phoneRepository.save(phoneList);
                phoneList = new ArrayList<>();
            }
        }

        phoneRepository.save(phoneList);
    }

}


