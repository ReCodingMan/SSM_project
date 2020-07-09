package com.cc.ssm.controller;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

@Component
@Aspect
public class LogAop {

    private Date visitTime; // 开始时间
    private Class clazz;    // 访问的类
    private Method method;  // 访问的方法

    /**
     * 前置通知 - 主要获取开始时间，执行的类，执行的哪一个方法
     * @param jp
     */
    @Before("execution(* com.cc.ssm.controller.*.*(..))")
    public void doBefore(JoinPoint jp) throws NoSuchMethodException {
        visitTime = new Date();             // 当前时间就是开始访问的时间
        clazz = jp.getTarget().getClass();  // 具体要访问的类
        String methodName = jp.getSignature().getName();   // 获取访问方法的名称
        Object[] args = jp.getArgs();       // 获取访问的方法的参数

        // 获取具体执行方法的 Method 对象
        if (args == null || args.length == 0){
            method = clazz.getMethod(methodName);   // 只能获取无参数的方法
        } else {
            Class[] classArgs = new Class[args.length];

            for (int i=0; i<args.length; i++) {
                classArgs[i] = args[i].getClass();
            }
            method = clazz.getMethod(methodName, classArgs);
        }
    }

    /**
     * 后置通知
     * @param jp
     */
    @After("execution(* com.cc.ssm.controller.*.*(..))")
    public void doAfter(JoinPoint jp) {
        long time = new Date().getTime() - visitTime.getTime(); // 获取访问时长

    }
}
