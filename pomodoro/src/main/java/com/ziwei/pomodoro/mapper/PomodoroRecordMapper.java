package com.ziwei.pomodoro.mapper;

import com.ziwei.pomodoro.entity.PomodoroRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PomodoroRecordMapper {

    @Insert("insert into pomodoro_record (duration,status,started_at,created_at,updated_at) " +
            "values (#{duration},#{status},NOW(),NOW(),NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void start(PomodoroRecord record);

    @Update("update pomodoro_record set status = #{status}, actual_duration = #{actualDuration}," +
            "ended_at = NOW(), updated_at = NOW() where id = #{id}")
    void end(PomodoroRecord record);

    @Select("select * from pomodoro_record where date(started_at) = curdate() order by started_at DESC ")
    List<PomodoroRecord> findTodayRecords();

    @Select("select * from pomodoro_record where status = 0 order by started_at DESC limit 1")
    PomodoroRecord findRunning();


}

