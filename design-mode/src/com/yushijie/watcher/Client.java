package com.yushijie.watcher;

import com.yushijie.watcher.observer.ConcreteObserver1;
import com.yushijie.watcher.observer.ConcreteObserver2;
import com.yushijie.watcher.observer.Observer;
import com.yushijie.watcher.subject.ConcreteSubject;
import com.yushijie.watcher.subject.Subject;

/**
 * @author yushijie
 * @version 1.0
 * @description client
 * @date 2022/10/14 11:38
 */
public class Client {
    public static void main(String[] args) {

        Subject subject = new ConcreteSubject();
        Observer observer1 = new ConcreteObserver1();
        Observer observer2 = new ConcreteObserver2();

        subject.addObserver(observer1);
        subject.addObserver(observer2);
        //被观察者发生改变，观察者进行动作
        subject.change();

    }
}
