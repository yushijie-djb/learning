package com.yushijie.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

public class CompletableFutureDemo {
    public static void main(String[] args) throws Exception{
        //testOne();//存在的问题：futuretask get会阻塞当前线程 isDone轮询会造成cpu空转
//        testTwo();//无返回值类型
        testThree();//有返回值类型
    }

    public static void testOne() throws Exception{
        FutureTask myFutureTask = new FutureTask(new MyCallable());
        Thread t = new Thread(myFutureTask);
        t.start();
        Object o = myFutureTask.get();
        System.out.println(o);
    }

    public static void testTwo() throws Exception{
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            //模拟业务运行1秒
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName());//ForkJoinPool.commonPool-worker-9
        });
        Void unused = voidCompletableFuture.get();
        System.out.println(unused);
    }

    public static void testThree() throws Exception{
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "supplier";
        });

        String result = stringCompletableFuture.get();
        System.out.println(result);
    }

}

class MyCallable implements Callable {
    @Override
    public Object call() throws Exception {
        System.out.println("lets call");
        return "call result";
    }
}
