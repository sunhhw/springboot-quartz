package com.shw.quartz.controller;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/16 17:20
 * @description 测试Spring自带的定时任务，缺点就是不能动态的控制定时任务的启动暂停
 */
@Component
@EnableScheduling//可以在启动类上注解也可以在当前文件
public class Test {


    //@Scheduled(cron = "0/1 * * * * ?")
    public static void hello() {
        System.out.println("spring的定时任务");
    }

}
