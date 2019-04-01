package com.kinson.myspring.annotation;

import java.lang.annotation.*;

/**
 * descripiton:
 *
 * @author: kinson(2219945910@qq.com)
 * @date: 2019/4/1
 * @time: 0:22
 * @modifier:
 * @since:
 */
//用于类、接口、枚举enum
@Target(ElementType.TYPE)
//生命周期为运行时
@Retention(RetentionPolicy.RUNTIME)
//javadoc
@Documented
public @interface Repository {

    /**
     * 作用于该注解的一个value属性
     * @return
     */
    String value();
}
