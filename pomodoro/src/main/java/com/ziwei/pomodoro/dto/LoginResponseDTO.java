package com.ziwei.pomodoro.dto;

import com.ziwei.pomodoro.entity.User;
import lombok.Data;

@Data
public class LoginResponseDTO {
    User user;
    String token;
}
