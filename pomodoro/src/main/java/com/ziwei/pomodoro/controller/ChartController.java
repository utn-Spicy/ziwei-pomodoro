package com.ziwei.pomodoro.controller;

import com.ziwei.pomodoro.entity.Chart;
import com.ziwei.pomodoro.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;


@RestController
@RequestMapping("/api/chart")
public class ChartController {

    @Autowired
    private ChartService chartService;

    @GetMapping("/create")
    public Chart create(
            @RequestParam("birthday") String birthday,
            @RequestParam(value = "birthTime", required = false )LocalTime birthTime){
        LocalDate date = LocalDate.parse(birthday);
        return chartService.createChart(date, birthTime);
    }
}
