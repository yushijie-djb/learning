package com.yushijie.decarator;

/**
 * @author yushijie
 * @version 1.0
 * @description 人(被装饰对象)
 * @date 2022/11/11 14:42
 */
public class People implements Action{
    @Override
    public void behavior() {
        System.out.println("吃饭");
        System.out.println("睡觉");
    }
}
