package com.itheima;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.domin.User;
import com.itheima.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootJpaApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() throws JsonProcessingException {
        //1.从redis中获取数据，数据形式为json字符串
        String userlistjson = redisTemplate.boundValueOps("user.findAll").get();
        //2.判断redis中是否存在数据
        if (null == userlistjson){
            //3.如果不存在，从数据库查询
            List<User> all = userRepository.findAll();
            //4.将查询到的数据存储到redis缓存中
            //将list集合转换成json格式的字符串，使用jackson进行转换
            ObjectMapper objectMapper = new ObjectMapper();
            userlistjson = objectMapper.writeValueAsString(all);
            redisTemplate.boundValueOps("user.findAll").set(userlistjson);

            System.out.println("=========从数据库查询获取user数据===========");
        }else {
            System.out.println("=========从redis缓存中查询获取user数据===========");
        }


        //4.将返回数数据进行打印
        System.out.println(userlistjson);
    }
}
