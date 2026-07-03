package com.ziwei.pomodoro.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PomodoroRecord {
    private Long id;
    private Integer duration;
    private Integer actualDuration;
    private Integer status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
