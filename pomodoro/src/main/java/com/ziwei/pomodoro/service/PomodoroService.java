package com.ziwei.pomodoro.service;

import com.ziwei.pomodoro.entity.PomodoroRecord;
import com.ziwei.pomodoro.mapper.PomodoroRecordMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class PomodoroService {

    @Autowired
    private PomodoroRecordMapper pomodoroRecordMapper;

    /**
     * 开始一个番茄钟
     * @param duration（分钟）
     * @return
     */
    @Operation(summary = "开始一个番茄钟")
    public PomodoroRecord start(Integer duration){
        PomodoroRecord record = new PomodoroRecord();
        record.setDuration(duration);
        record.setStatus(0);//RUNNING
        pomodoroRecordMapper.start(record);
        return record;
    }

    /**
     * 完成一个番茄钟
     * @param id
     * @param actualDuration
     * @return
     */
    @Operation(summary = "完成一个番茄钟")
    public PomodoroRecord complete(Long id,Integer actualDuration){
        PomodoroRecord record = new PomodoroRecord();
        record.setId(id);
        record.setStatus(1);//COMPLETED
        record.setActualDuration(actualDuration);
        pomodoroRecordMapper.end(record);
        return record;
    }

    /**
     * 中断一个番茄钟
     * @param id
     * @return
     */
    @Operation(summary = "中断一个番茄钟")
    public PomodoroRecord interrupt(Long id){
        PomodoroRecord record = new PomodoroRecord();
        record.setId(id);
        record.setStatus(2);//INTERRUPTED
        pomodoroRecordMapper.end(record);
        return record;
    }

    /**
     * 查询今日番茄钟记录
     * @return
     */
    @Operation(summary = "查询今日番茄钟记录")
    public List<PomodoroRecord> getTodayRecords(){
        return pomodoroRecordMapper.findTodayRecords();
    }

    /**
     * 查询当前正在运行中的番茄钟
     * @return
     */
    @Operation(summary = "查询当前正在运行中的番茄钟")
    public PomodoroRecord getRunningRecord(){
        return pomodoroRecordMapper.findRunning();
    }
}
