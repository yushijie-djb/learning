package com.yushijie.watcher.subject;

import com.yushijie.watcher.observer.Observer;

import java.util.Vector;

/**
 * @author yushijie
 * @version 1.0
 * @description 被观察者抽象类
 * @date 2022/10/14 11:08
 */
public abstract class Subject {

    private Vector<Observer> obs = new Vector<>();

    public void addObserver(Observer obs){
        this.obs.add(obs);
    }

    public void delObserver(Observer obs){
        this.obs.remove(obs);
    }

    protected void notifyObserver(){
        for(Observer o: obs){
            o.update();
        }
    }

    public abstract void change();

}
