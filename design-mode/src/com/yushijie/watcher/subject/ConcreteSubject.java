package com.yushijie.watcher.subject;

/**
 * @author yushijie
 * @version 1.0
 * @description 具体的被观察者
 * @date 2022/10/14 11:23
 */
public class ConcreteSubject extends Subject {

    @Override
    public void change() {
        System.out.println("ConcreteSubject1 doSomething");
        notifyObserver();
    }

}
