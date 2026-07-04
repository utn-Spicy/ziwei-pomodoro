package com.ziwei.pomodoro.dto;

import com.ziwei.pomodoro.entity.PomodoroRecord;
import lombok.Data;

@Data
public class PomodoroResultDTO {
    PomodoroRecord record;
    String suggestion;
}
