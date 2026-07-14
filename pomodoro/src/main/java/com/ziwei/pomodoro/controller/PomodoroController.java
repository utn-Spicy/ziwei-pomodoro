package com.ziwei.pomodoro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ziwei.pomodoro.common.Result;
import com.ziwei.pomodoro.entity.PomodoroRecord;
import com.ziwei.pomodoro.service.PomodoroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pomodoro")
@Tag(name = "番茄钟管理")
public class PomodoroController {

    @Autowired
    private PomodoroService pomodoroService;


    /**
     *开始一个番茄钟
     * @param duration
     * @return
     */
    @Operation(summary = "开始一个番茄钟")
    @PostMapping("/start")
    public Result<PomodoroRecord> start(@RequestParam Integer duration) throws JsonProcessingException {
        return Result.success(pomodoroService.start(duration));
    }

    /**
     * 完成一个番茄钟
     * @param id
     * @param actualDuration
     * @return
     */
    @Operation(summary = "完成一个番茄钟")
    @PutMapping("/{id}/complete")
    public Result<PomodoroRecord> complete(@PathVariable Long id, @RequestParam Integer actualDuration){
        return Result.success(pomodoroService.complete(id,actualDuration));
    }

    /**
     * 中断一个番茄钟
     * @param id
     * @return
     */
    @Operation(summary = "中断一个番茄钟")
    @PutMapping("/{id}/interrupt")
    public Result<PomodoroRecord> interrupt(@PathVariable Long id){
        return Result.success(pomodoroService.interrupt(id));
    }

    /**
     * 查询今日番茄钟记录
     * @return
     */
    @Operation(summary = "查询今日番茄钟记录")
    @GetMapping("/today")
    public Result<List<PomodoroRecord>> getTodayRecords(){
        return Result.success(pomodoroService.getTodayRecords());
    }

    /**
     * 查询当前正在运行中的番茄钟
     * @return
     */
    @Operation(summary = "查询当前正在运行中的番茄钟")
    @GetMapping("/running")
    public Result<PomodoroRecord> getRunningRecord() throws JsonProcessingException {
        return  Result.success(pomodoroService.getRunningRecord());
    }
}
