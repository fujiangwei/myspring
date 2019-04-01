package com.kinson.myspring.service.impl;

import com.kinson.myspring.annotation.Service;
import com.kinson.myspring.service.UserService;

/**
 * descripiton:
 *
 * @author: kinson(2219945910@qq.com)
 * @date: 2019/4/1
 * @time: 12:01
 * @modifier:
 * @since:
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Override
    public void hello() {
        System.out.println("hello, myspring");
    }
}
