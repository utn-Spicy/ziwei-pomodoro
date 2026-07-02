package com.ziwei.pomodoro.service;

import com.ziwei.pomodoro.entity.Chart;
import com.ziwei.pomodoro.mapper.ChartMapper;
import com.ziwei.pomodoro.util.ZiWeiCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class ChartService {
    @Autowired
    private ChartMapper chartMapper;

    /*
    创建命盘记录：算排盘->存数据库->返回
     */
    public Chart createChart(LocalDate birthday, LocalTime birthTime){
        //1.调排盘引擎算
        ZiWeiCalculator.ChartResult result;
        if (birthTime !=null){
            result = ZiWeiCalculator.calculate(birthday,birthTime);
        }else{
            result = ZiWeiCalculator.calculate(birthday);
        }

        //2.封装实体
        Chart chart = new Chart();
        chart.setBirthday(birthday);
        chart.setBirthTime(birthTime);
        chart.setMainStar(result.mainStar != null ? result.mainStar : "");
        chart.setPersonalityType(result.strategy.personalityType);
        chart.setRecommendDuration(result.strategy.recommendDuration);


        //3.保存到数据库
        chartMapper.insert(chart);

        return chart;
    }


}
