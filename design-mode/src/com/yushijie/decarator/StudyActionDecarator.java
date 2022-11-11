package com.yushijie.decarator;

/**
 * @author yushijie
 * @version 1.0
 * @description 学习装饰类
 * @date 2022/11/11 14:53
 */
public class StudyActionDecarator extends ActionDecarator{

    public StudyActionDecarator(Action action) {
        super(action);
    }

    @Override
    public void behavior() {
        super.behavior();
        //增强功能
        System.out.println("学习");
    }

}
