package com.ziwei.pomodoro.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class User {
    private Long userId;
    private String userName;
    private LocalDate birthday;
    private LocalTime birthTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
