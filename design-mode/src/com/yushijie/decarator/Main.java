package com.yushijie.decarator;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2022/11/11 14:57
 */
public class Main {
    public static void main(String[] args) {
        Action peopleAction = new People();
        System.out.println("装饰前=====");
        peopleAction.behavior();
        System.out.println("装饰后=====");
        ActionDecarator studyActionDecarator = new StudyActionDecarator(peopleAction);
        studyActionDecarator.behavior();
        System.out.println("再装饰后=====");
        ActionDecarator doLoveActionDecarator = new DoLoveActionDecarator(studyActionDecarator);
        doLoveActionDecarator.behavior();
    }
}
