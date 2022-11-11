package com.yushijie.decarator;

/**
 * @author yushijie
 * @version 1.0
 * @description hehe 你懂的
 * @date 2022/11/11 15:01
 */
public class DoLoveActionDecarator extends ActionDecarator{

    public DoLoveActionDecarator(Action action) {
        super(action);
    }

    @Override
    public void behavior() {
        super.behavior();
        System.out.println("hehe do love");
    }
}
