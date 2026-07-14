package com.ziwei.pomodoro.service;

import com.ziwei.pomodoro.entity.User;
import com.ziwei.pomodoro.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Transactional
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     *登录或注册用户
     * @param user
     * @return
     */
    public User loginOrRegister(User user){
        User existingUser = userMapper.queryUser(user);
        if (existingUser != null){ return existingUser; }
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.addUser(user);
        return user;
    }
}
