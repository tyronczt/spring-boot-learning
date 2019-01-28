package com.tyron.springboot;

import com.tyron.springboot.bean.Person;
import org.springframework.context.ApplicationContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBoot02ConfigApplicationTests {

    @Autowired
    Person person;

    @Autowired
    ApplicationContext ioc;

    @Test
    public void testService() {
        boolean containsBean = ioc.containsBean("helloService02");
        System.out.println("containsBean：" + containsBean);
    }

    @Test
    public void contextLoads() {
        System.out.println(person);
    }

}

