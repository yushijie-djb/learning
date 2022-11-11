package com.yushijie.decarator;

/**
 * @author yushijie
 * @version 1.0
 * @description 抽象Action装饰对象 持有一个Action实例
 * @date 2022/11/11 14:46
 */
public abstract class ActionDecarator implements Action{
    //注意权限修饰符
    protected Action action;

    public ActionDecarator(Action action) {
        this.action = action;
    }

    @Override
    public void behavior() {
        if (action != null) {
            action.behavior();
        }
    }

}
