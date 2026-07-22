package com.ziwei.pomodoro.mapper;

import com.ziwei.pomodoro.annotation.AutoFill;
import com.ziwei.pomodoro.entity.PomodoroRecord;
import com.ziwei.pomodoro.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PomodoroRecordMapper {

    @AutoFill(OperationType.INSERT)
    @Insert("insert into pomodoro_record (user_id,duration,status,started_at,created_at,updated_at) " +
            "values (#{userId},#{duration},#{status},#{startedAt},#{createdAt},#{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void start(PomodoroRecord record);

    @AutoFill(OperationType.UPDATE)
    @Update("update pomodoro_record set status = #{status}, actual_duration = #{actualDuration}," +
            "ended_at = #{endedAt}, updated_at = #{updatedAt} where id = #{id}")
    void end(PomodoroRecord record);

    @Select("select * from pomodoro_record where date(started_at) = curdate() and user_id = #{userId} order by started_at DESC ")
    List<PomodoroRecord> findTodayRecords(@Param("userId") Long userId);

    @Select("select * from pomodoro_record where status = 0 order by started_at DESC limit 1")
    PomodoroRecord findRunning();


}

