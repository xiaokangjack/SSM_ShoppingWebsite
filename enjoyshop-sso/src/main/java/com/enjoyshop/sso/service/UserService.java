package com.enjoyshop.sso.service;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enjoyshop.common.service.RedisService;
import com.enjoyshop.sso.mapper.UserMapper;
import com.enjoyshop.sso.pojo.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserMapper userMapper;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Boolean check(String param, Integer type) {
        if (type < 1 || type > 3) {
            return null;
        }
        User record = new User();
        switch (type) {
        case 1:
            record.setUsername(param);
            break;
        case 2:
            record.setPhone(param);
            break;
        case 3:
            record.setEmail(param);
            break;
        default:
            break;
        }
        return this.userMapper.selectOne(record) == null;
    }

    public Boolean saveUser(User user) {
        user.setId(null);
        user.setCreated(new Date());
        user.setUpdated(user.getCreated());

        // 密码通过MD5进行加密处理
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));

        return this.userMapper.insert(user) == 1;
    }

    public String doLogin(String username, String password) throws Exception {
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        if (null == user) {
            return null;
        }
        // 比对密码是否正确
        if (!StringUtils.equals(DigestUtils.md5Hex(password), user.getPassword())) {
            return null;
        }

        // 登录成功
        // 生存token
        String token = DigestUtils.md5Hex(System.currentTimeMillis() + username);

        // 将用户数据保存到redis中
        this.redisService.set("TOKEN_" + token, MAPPER.writeValueAsString(user), 60 * 30);

        return token;
    }

    public User queryUserByToken(String token) {
        String key = "TOKEN_" + token;
        String jsonData = this.redisService.get(key);
        if (StringUtils.isEmpty(jsonData)) {
            return null;
        }
        try {
            // 刷新用户的生存时间(非常重要)
            this.redisService.expire(key, 60 * 30);
            return MAPPER.readValue(jsonData, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
