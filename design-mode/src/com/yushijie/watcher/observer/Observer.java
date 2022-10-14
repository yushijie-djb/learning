package com.yushijie.watcher.observer;

/**
 * @author yushijie
 * @version 1.0
 * @description 观察者接口
 * @date 2022/10/14 11:07
 */
public interface Observer {

    /**
     * @author yushijie
     * @description  当被观察者(Subject)发生变化时，调用此方法通知观察者(Observer)
     * @date 2022/10/14 11:15
     * @return void
     */
    public void update();

}
