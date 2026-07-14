package com.ziwei.pomodoro.controller;

import com.ziwei.pomodoro.common.Result;
import com.ziwei.pomodoro.service.ChartService;
import com.ziwei.pomodoro.service.SuggestionService;
import com.ziwei.pomodoro.util.ZiWeiCalculator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/test/redis")
    public String testRedis(){
        //1.存一个key到Redis
        stringRedisTemplate.opsForValue().set("test_key","test_value");

        //2.读出来
        String value = stringRedisTemplate.opsForValue().get("test_key");

        //3.返回读到的值
        return "从Redis读到的值是：" + value;
    }

    /**
     * AI建议
     * @param birthday
     * @param status
     * @return
     */
    @Operation(summary = "AI建议",description = "完成、休息、中断时弹出")
    @GetMapping
    public Result<String> suggestion(@RequestParam LocalDate birthday, @RequestParam Integer status){
        ZiWeiCalculator.ChartResult result = chartService.analyze(birthday,null);
        String suggestion = suggestionService.generateSuggestion(result.strategy.personalityType,result.strategy.element,status);
        return Result.success(suggestion);
    }


}
