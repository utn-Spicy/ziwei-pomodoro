package com.ziwei.pomodoro.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Schema(description = "创建命盘信息")
public class Chart {
    private Long id;
    private LocalDate birthday;
    private LocalTime birthTime;
    private String mainStar;
    private String personalityType;
    private Integer recommendDuration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /*
    测试data：
    public static void main(String[] args) {
        Chart chart = new Chart();
        chart.setId(100L);
        chart.setMainStar("武曲天府");

        System.out.println(chart.getId());
        System.out.println(chart.getMainStar());
    }*/
}


