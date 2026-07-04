package com.ziwei.pomodoro.controller;

import com.ziwei.pomodoro.service.ChartService;
import com.ziwei.pomodoro.service.SuggestionService;
import com.ziwei.pomodoro.util.ZiWeiCalculator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/suggestion")
@Tag(name = "AI")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private ChartService chartService;

    /**
     * AI建议
     * @param birthday
     * @param status
     * @return
     */
    @Operation(summary = "AI建议",description = "完成、休息、中断时弹出")
    @GetMapping
    public String suggestion(@RequestParam LocalDate birthday,@RequestParam Integer status){
        ZiWeiCalculator.ChartResult result = chartService.analyze(birthday,null);
        String suggestion = suggestionService.generateSuggestion(result.strategy.personalityType,result.strategy.element,status);
        return suggestion;
    }

}
