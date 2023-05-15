package com.yushijie.juc;

import java.util.function.Function;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2023/5/15 13:58
 */
public class FunctionDemo {
    public static void main(String[] args) {
        MyFunction myFunction = new MyFunction();
        System.out.println(myFunction.apply(1));
    }
}

class MyFunction implements Function<Integer, String> {
    @Override
    public String apply(Integer integer) {
        return String.valueOf(integer);
    }
}
