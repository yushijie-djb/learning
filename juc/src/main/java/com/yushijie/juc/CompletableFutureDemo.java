package com.yushijie.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class CompletableFutureDemo {
    public static void main(String[] args) throws Exception{
       testOne();//存在的问题：futuretask get会阻塞当前线程 isDone轮询会造成cpu空转
    }

    public static void testOne() throws Exception{
        FutureTask myFutureTask = new FutureTask(new MyCallable());
        Thread t = new Thread(myFutureTask);
        t.start();
        Object o = myFutureTask.get();
        System.out.println(o);
    }
}

class MyCallable implements Callable {
    @Override
    public Object call() throws Exception {
        System.out.println("lets call");
        return "call result";
    }
}
