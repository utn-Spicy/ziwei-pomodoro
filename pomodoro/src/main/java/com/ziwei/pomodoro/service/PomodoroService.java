package com.ziwei.pomodoro.service;

import com.ziwei.pomodoro.entity.PomodoroRecord;
import com.ziwei.pomodoro.mapper.PomodoroRecordMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Transactional
@Service
public class PomodoroService {

    @Autowired
    private PomodoroRecordMapper pomodoroRecordMapper;

    @Autowired
    private SuggestionService suggestionService;

    /**
     * 线程池成员变量
     */
    private final ThreadPoolExecutor aiExecutor = new ThreadPoolExecutor(
            2,40,50, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(10)
    );

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
     *
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

    /**
     *AI异步调用
     * @param recordID
     * @param personalityType
     * @param element
     * @param status
     */
    @Operation(summary = "AI异步调用")
    public void generateSuggestionAsync(Long recordID, String personalityType, String element, Integer status){
        aiExecutor.execute(() -> {
            try {
                String suggestion = suggestionService.generateSuggestion(
                        personalityType, element, status
                );
                log.info("AI语录生成成功：" + suggestion);
            } catch (Exception e){
                log.error("AI语录生成失败：" + e.getMessage());
            }
        });
    }

}
