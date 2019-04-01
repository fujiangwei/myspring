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
//用于类、接口、枚举enum、方法
@Target({ElementType.TYPE, ElementType.METHOD})
//生命周期为运行时
@Retention(RetentionPolicy.RUNTIME)
//javadoc
@Documented
public @interface RequestMapping {

    /**
     * 作用于该注解的一个value属性
     *
     * @return
     */
    String value();
}
