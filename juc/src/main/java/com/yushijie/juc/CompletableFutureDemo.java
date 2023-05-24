package com.yushijie.juc;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class CompletableFutureDemo {
    public static void main(String[] args) throws Exception{
        //testOne();//存在的问题：futuretask get会阻塞当前线程 isDone轮询会造成cpu空转
//        testTwo();//无返回值类型
//        testThree();//有返回值类型
//        testFour();
//        testFive();
//        testSix();
//        testSeven();
//        testEight();
        testNine();
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
    //thenRun()
    public static void testFour() throws Exception{
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.runAsync(() -> {
            System.out.println("1");
            System.out.println(Thread.currentThread().getName());
        }, executor);
        CompletableFuture<Void> voidCompletableFuture2 = voidCompletableFuture1.thenRun(() -> {
            System.out.println("2");
            System.out.println(Thread.currentThread().getName());
        });
        CompletableFuture<Void> voidCompletableFuture3 = voidCompletableFuture2.thenRun(() -> {
            System.out.println("3");
            System.out.println(Thread.currentThread().getName());
        });
        voidCompletableFuture3.get();
        executor.shutdown();
    }

    //thenAccept()
    //第一个任务执行完成后，执行第二个回调方法任务，会将该任务的执行结果，作为入参，传递到回调方法中，但是回调方法是没有返回值的
    public static void testFive() throws Exception {
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            return "1";
        });
        CompletableFuture<Void> voidCompletableFuture = stringCompletableFuture.thenAccept(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });
        voidCompletableFuture.get();
    }

    //thenApply()
    //第一个任务执行完成后，执行第二个回调方法任务，会将该任务的执行结果，作为入参，传递到回调方法中，并且回调方法是有返回值的。
    public static void testSix() throws Exception {
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            return "1";
        });
        CompletableFuture<String> stringCompletableFuture1 = stringCompletableFuture.thenApply(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s + "apply";
            }
        });
        System.out.println(stringCompletableFuture1.get());
    }

    //exceptionally()
    //某个任务执行异常时，执行的回调方法;并且有抛出异常作为参数，传递到回调方法。
    public static void testSeven() throws Exception {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("我ERROR了");
        });
        CompletableFuture<String> exceptionally = completableFuture.exceptionally((e) -> {
            return e.getMessage();
        });
        System.out.println(exceptionally.get());
    }

    //whenComplete()
    //某个任务执行完成后，执行的回调方法，无返回值；并且whenComplete方法返回的CompletableFuture的result是上个任务的结果。
    public static void testEight() throws Exception{
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                return "我不是一个粉刷匠";
            }
            return "我是一个粉刷匠";
        });

        CompletableFuture<String> whenCompletableFuture = stringCompletableFuture.whenComplete((s, e) -> {
            System.out.println(s + "，粉刷本领强");
        });

        System.out.println(whenCompletableFuture.get());
    }

    //handle()
    //某个任务执行完成后，执行回调方法，并且是有返回值的;并且handle方法返回的CompletableFuture的result是回调方法执行的结果
    public static void testNine() throws Exception{
        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                return "我不是一个粉刷匠";
            }
            return "我是一个粉刷匠";
        });

        CompletableFuture<String> handleCompletableFuture = stringCompletableFuture.handle((s, e) -> {
            System.out.println(s + "，粉刷本领强");
            return s + "，粉刷本领强";
        });

        System.out.println(handleCompletableFuture.get());
    }
}

class MyCallable implements Callable {
    @Override
    public Object call() throws Exception {
        System.out.println("lets call");
        return "call result";
    }
}
