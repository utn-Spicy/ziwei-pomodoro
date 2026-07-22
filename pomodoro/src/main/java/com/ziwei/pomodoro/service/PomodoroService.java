package com.ziwei.pomodoro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ziwei.pomodoro.common.BaseContext;
import com.ziwei.pomodoro.entity.PomodoroRecord;
import com.ziwei.pomodoro.mapper.PomodoroRecordMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
    public PomodoroRecord start(Integer duration) throws JsonProcessingException {
        PomodoroRecord record = new PomodoroRecord();
        Long userId = BaseContext.getCurrentId();
        record.setDuration(duration);
        record.setStatus(0);//RUNNING

        pomodoroRecordMapper.start(record);
        stringRedisTemplate.opsForValue().set("pomodoro:running:" + userId, objectMapper.writeValueAsString(record),duration+1,TimeUnit.MINUTES);
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
        Long userId = BaseContext.getCurrentId();
        PomodoroRecord record = new PomodoroRecord();
        record.setId(id);
        record.setStatus(1);//COMPLETED
        record.setActualDuration(actualDuration);
        record.setEndedAt(LocalDateTime.now());
        pomodoroRecordMapper.end(record);
        stringRedisTemplate.delete("pomodoro:running:" + userId);
        return record;
    }

    /**
     * 中断一个番茄钟
     * @param id
     * @return
     */
    @Operation(summary = "中断一个番茄钟")
    public PomodoroRecord interrupt(Long id){
        Long userId = BaseContext.getCurrentId();
        PomodoroRecord record = new PomodoroRecord();
        record.setId(id);
        record.setStatus(2);//INTERRUPTED
        record.setEndedAt(LocalDateTime.now());
        pomodoroRecordMapper.end(record);
        stringRedisTemplate.delete("pomodoro:running:" + userId);
        return record;
    }

    /**
     * 查询今日番茄钟记录
     * @return
     */
    @Operation(summary = "查询今日番茄钟记录")
    public List<PomodoroRecord> getTodayRecords(){
        return pomodoroRecordMapper.findTodayRecords(BaseContext.getCurrentId());
    }

    /**
     * 查询当前正在运行中的番茄钟
     * @return
     */
    @Operation(summary = "查询当前正在运行中的番茄钟")
    public PomodoroRecord getRunningRecord() throws JsonProcessingException {
        Long userId = BaseContext.getCurrentId();
        String runningRecord = stringRedisTemplate.opsForValue().get("pomodoro:running:" + userId);
        if (runningRecord == null){
            return pomodoroRecordMapper.findRunning();
        } else {
            return objectMapper.readValue(runningRecord, PomodoroRecord.class);
        }
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
