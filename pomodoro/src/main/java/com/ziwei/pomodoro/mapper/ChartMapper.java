package com.ziwei.pomodoro.mapper;

import com.ziwei.pomodoro.entity.Chart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChartMapper {
    @Insert("INSERT INTO  ziwei_chart (birthday,birth_time,main_star,personality_type,recommend_duration,created_at,updated_at) " +
            "values (#{birthday},#{birthTime},#{mainStar},#{personalityType},#{recommendDuration},NOW(),NOW())")
    void insert(Chart chart) ;
}
