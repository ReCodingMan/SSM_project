package com.cc.ssm.controller;

import com.cc.ssm.domain.SysLog;
import com.cc.ssm.service.ISysLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Component
@Aspect
public class LogAop {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ISysLogService sysLogService;

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
    public void doAfter(JoinPoint jp) throws Exception {
        long time = new Date().getTime() - visitTime.getTime(); // 获取访问时长

        String url = "";

        // 获取 url
        if (clazz != null && method != null && clazz != LogAop.class) {

            // 1、获取类上的 @RequestMapping("/orders")
            RequestMapping classAnnotation = (RequestMapping) clazz.getAnnotation(RequestMapping.class);

            if (classAnnotation != null) {
                String classValue = classAnnotation.value()[0];

                // 2、获取方法上的 @RequestMapping(xxx)
                RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                if (methodAnnotation != null) {
                    String methodValue = methodAnnotation.value()[0];
                    url = classValue + methodValue;

                    // 获取访问的IP
                    String ip = request.getRemoteAddr();

                    // 获取当前操作的用户
                    SecurityContext context = SecurityContextHolder.getContext(); // 从上下文中获取当前登录的用户
                    User user = (User) context.getAuthentication().getPrincipal();
                    String username = user.getUsername();

                    // 将日志相关信息封装到 SysLog 对象
                    SysLog sysLog = new SysLog();
                    sysLog.setExecutionTime(time);  // 执行时长
                    sysLog.setIp(ip);   // ip地址
                    sysLog.setMethod("【类名】" + clazz.getName() + "【方法名】"+ method.getName());
                    sysLog.setUrl(url);
                    sysLog.setUsername(username);
                    sysLog.setVisitTime(visitTime);

                    // 调用 Service 完成操作
                    sysLogService.save(sysLog);
                }
            }
        }
    }
}
