package com.allweing;

import com.allweing.service.OrderService;
import com.allweing.spring.AllweingApplicationContext;

/**
 * @auther: zzzgyu
 */

public class Test {
    public static void main(String[] args) {
        //启动spring
        AllweingApplicationContext applicationContext = new AllweingApplicationContext(AppConfig.class);


        //getBean
        OrderService orderService =(OrderService) applicationContext.getBean("orderService");
        OrderService orderService1 =(OrderService) applicationContext.getBean("orderService");

        System.out.println(applicationContext.getBean("orderService"));
        System.out.println(applicationContext.getBean("orderService"));
        System.out.println(applicationContext.getBean("orderService"));
        System.out.println(applicationContext.getBean("orderService"));


    }
}
