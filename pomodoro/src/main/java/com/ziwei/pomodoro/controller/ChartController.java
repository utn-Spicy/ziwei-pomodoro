package com.ziwei.pomodoro.controller;

import com.ziwei.pomodoro.entity.Chart;
import com.ziwei.pomodoro.service.ChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;


@RestController
@RequestMapping("/api/chart")
@Tag(name = "命盘信息获取",description = "两种获取方式（有无出生时辰）")
public class ChartController {

    @Autowired
    private ChartService chartService;

    /**
     * 命盘信息获取，创建命盘分析
     * @param birthday
     * @param birthTime
     * @return
     */
    @Operation(summary = "创建命盘分析", description = "根据出生日期（可选出生时间）获取命盘和番茄钟策略")
    @GetMapping("/create")
    public Chart create(
            @RequestParam("birthday") String birthday,
            @RequestParam(value = "birthTime", required = false )LocalTime birthTime){
        LocalDate date = LocalDate.parse(birthday);
        return chartService.createChart(date, birthTime);
    }
}
