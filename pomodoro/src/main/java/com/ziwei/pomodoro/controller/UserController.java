package com.ziwei.pomodoro.controller;

import com.ziwei.pomodoro.dto.LoginResponseDTO;
import com.ziwei.pomodoro.entity.User;
import com.ziwei.pomodoro.service.UserService;
import com.ziwei.pomodoro.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户信息操作")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param userName
     * @param birthday
     * @return
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public LoginResponseDTO  logIn(@RequestParam String userName, @RequestParam LocalDate birthday){
        User user =new User();
        user.setUserName(userName);
        user.setBirthday(birthday);
        User nowUser = userService.loginOrRegister(user);
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken(JwtUtil.createToken(nowUser.getUserId()));
        loginResponseDTO.setUser(nowUser);
        return loginResponseDTO;
    }

}
