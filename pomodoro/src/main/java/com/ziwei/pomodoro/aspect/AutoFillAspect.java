package com.ziwei.pomodoro.aspect;

import com.ziwei.pomodoro.annotation.AutoFill;
import com.ziwei.pomodoro.common.BaseContext;
import com.ziwei.pomodoro.enumeration.OperationType;
import cn.hutool.core.util.ObjectUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoFillAspect {
    /**
     *切入点
     */
    @Pointcut("execution(* com.ziwei.pomodoro.*.*.*(..)) && @annotation(com.ziwei.pomodoro.annotation.AutoFill)")
    public void autoFillPointCut(){

    }

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        //1.获取当前被拦截的方法上的数据库操作类型
        //方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取方法上的注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        //获取注解内的操作类型
        OperationType operationType = autoFill.value();

        //2.获取到当前被拦截方法的参数——实体对象
        Object[] args = joinPoint.getArgs();
        if (ObjectUtil.isEmpty(args)){
            return;
        }
        //获取方法的第一个参数（约定）
        Object entity = args[0];

        //3.准备赋值的数据
        LocalDateTime nowDate = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //4.根据当前不同的操作类型，为对应的属性通过反射来赋值
        try {
            if (operationType.equals(OperationType.INSERT)) {
                Method setCreatedAt = entity.getClass().getMethod("setCreatedAt",LocalDateTime.class);
                setCreatedAt.invoke(entity,nowDate);
                Method setUpdatedAt = entity.getClass().getMethod("setUpdatedAt",LocalDateTime.class);
                setUpdatedAt.invoke(entity,nowDate);
                Method setStartedAt = entity.getClass().getMethod("setStartedAt",LocalDateTime.class);
                setStartedAt.invoke(entity,nowDate);
                Method setUserId = entity.getClass().getMethod("setUserId",Long.class);
                setUserId.invoke(entity,currentId);
            }else {
                Method setUpdatedAt = entity.getClass().getMethod("setUpdatedAt",LocalDateTime.class);
                setUpdatedAt.invoke(entity,nowDate);
                Method setUserId = entity.getClass().getMethod("setUserId",Long.class);
                setUserId.invoke(entity,currentId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
