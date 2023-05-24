package com.yushijie.juc;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2023/5/24 15:21
 */
public class CompletableFutureCombinationDemo {

    public static void main(String[] args) throws Exception{
//        testAnd();
//        testOr();
        testAllOf();
    }

    //thenCombine()会将两个任务的执行结果作为方法入参，传递到指定方法中，且有返回值
    //thenAcceptBoth()会将两个任务的执行结果作为方法入参，传递到指定方法中，且无返回值
    //runAfterBoth()不会把执行结果当做方法入参，且没有返回值。
    public static void testAnd() throws Exception{
        CompletableFuture<String> firstStringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "这是第一个异步任务执行结果。";
        });

        CompletableFuture<String> secondStringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "这是第二个异步任务执行结果。";
        });

        CompletableFuture<Object> combineCompletableFuture = secondStringCompletableFuture.thenCombine(firstStringCompletableFuture, new BiFunction<String, String, Object>() {
            @Override
            public Object apply(String s, String s2) {
                System.out.println(s + s2);
                return null;
            }
        });
        combineCompletableFuture.get();
    }

    //将两个CompletableFuture组合起来，只要其中一个执行完了,就会执行某个任务。
    //applyToEither()会将已经执行完成的任务，作为方法入参，传递到指定方法中，且有返回值
    //acceptEither()会将已经执行完成的任务，作为方法入参，传递到指定方法中，且无返回值
    //runAfterEither()不会把执行结果当做方法入参，且没有返回值。
    public static void testOr() throws Exception{
        CompletableFuture<String> firstStringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "这是第一个异步任务执行结果。";
        });

        CompletableFuture<String> secondStringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "这是第二个异步任务执行结果。";
        });

        CompletableFuture<Void> orCompletableFuture = secondStringCompletableFuture.acceptEither(firstStringCompletableFuture, (s) -> {
            System.out.println("先获得的结果为：" + s);
        });
        orCompletableFuture.get();
    }

    //所有任务都执行完成后，才执行 allOf返回的CompletableFuture。如果任意一个任务异常，allOf的CompletableFuture，执行get方法，会抛出异常
    public static void testAllOf() throws Exception {
        CompletableFuture<String> firstStringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000L);
                System.out.println("这是第一个异步任务执行结果");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "这是第一个异步任务执行结果。";
        });

        CompletableFuture<String> secondStringCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000L);
                System.out.println("这是第二个异步任务执行结果");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "这是第二个异步任务执行结果。";
        });

        CompletableFuture<Void> allOfCompletableFuture = CompletableFuture.allOf(firstStringCompletableFuture, secondStringCompletableFuture).thenRun(() -> {
            System.out.println("all of");
        });
        allOfCompletableFuture.get();
    }

    //todo thenCompose
}
