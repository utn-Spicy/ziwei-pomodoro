package com.ziwei.pomodoro.mapper;

import com.ziwei.pomodoro.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user where user_name = #{userName} and birthday = #{birthday} ")
    User queryUser(User user);

    @Insert("insert into user (user_id,user_name,birthday,birth_time,created_at,updated_at)"+
            "values (#{userId},#{userName},#{birthday},#{birthTime},now(),now())")
    @Options(useGeneratedKeys = true , keyProperty = "userId")
    void addUser (User user);

}
