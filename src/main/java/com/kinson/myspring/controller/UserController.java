package com.kinson.myspring.controller;

import com.kinson.myspring.annotation.Controller;
import com.kinson.myspring.annotation.Qualifier;
import com.kinson.myspring.annotation.RequestMapping;
import com.kinson.myspring.service.UserService;

/**
 * descripiton:
 *
 * @author: kinson(2219945910@qq.com)
 * @date: 2019/4/1
 * @time: 11:52
 * @modifier:
 * @since:
 */
@Controller("userController")
@RequestMapping("/user")
public class UserController {

    @Qualifier("userServiceImpl")
    private UserService userService;

    @RequestMapping(value = "/hello")
    public String hello() {
        System.out.println("UserController.hello");
        userService.hello();
        return "UserController.hello";
    }
}
