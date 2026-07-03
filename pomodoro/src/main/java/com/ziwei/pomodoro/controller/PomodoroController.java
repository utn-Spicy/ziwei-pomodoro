package com.ziwei.pomodoro.controller;

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
    public PomodoroRecord start(@RequestParam Integer duration){
        return pomodoroService.start(duration);
    }

    /**
     * 完成一个番茄钟
     * @param id
     * @param actualDuration
     * @return
     */
    @Operation(summary = "完成一个番茄钟")
    @PutMapping("/{id}/complete")
    public PomodoroRecord complete(@PathVariable Long id,@RequestParam Integer actualDuration){
        return pomodoroService.complete(id,actualDuration);
    }

    /**
     * 中断一个番茄钟
     * @param id
     * @return
     */
    @Operation(summary = "中断一个番茄钟")
    @PutMapping("/{id}/interrupt")
    public PomodoroRecord interrupt(@PathVariable Long id){
        return pomodoroService.interrupt(id);
    }

    /**
     * 查询今日番茄钟记录
     * @return
     */
    @Operation(summary = "查询今日番茄钟记录")
    @GetMapping("/today")
    public List<PomodoroRecord> getTodayRecords(){
        return pomodoroService.getTodayRecords();
    }

    /**
     * 查询当前正在运行中的番茄钟
     * @return
     */
    @Operation(summary = "查询当前正在运行中的番茄钟")
    @GetMapping("/running")
    public PomodoroRecord getRunningRecord(){
        return  pomodoroService.getRunningRecord();
    }
}
