package com.yushijie.watcher.observer;

/**
 * @author yushijie
 * @version 1.0
 * @description 具体的观察者
 * @date 2022/10/14 11:28
 */
public class ConcreteObserver1 implements Observer{

    @Override
    public void update() {
        System.out.println("ConcreteObserver1 update");
    }

}
