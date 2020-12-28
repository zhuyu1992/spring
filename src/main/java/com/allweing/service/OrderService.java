package com.allweing.service;

import com.allweing.spring.*;

/**
 * @auther: zzzgyu
 */

@Component(value = "orderService")
@Scope("prototype")
public class OrderService implements InitializingBean, BeanNameAware {


    @Autowired
    private UserService userService;

    private String beanName;


    public void test() {
        System.out.println(this);
    }

    public void afterPropertiesSet() {
        System.out.println("初始化");
    }
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


}
