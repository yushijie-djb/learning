package com.yushijie.juc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2023/6/26 18:06
 */
public class CountDownLatchDemo {
    public static void main(String[] args) {
//        demo1();
        demo2();
    }

    public static void demo1() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    //准备完毕……运动员都阻塞在这，等待号令
                    countDownLatch.await();
                    String parter = "【" + Thread.currentThread().getName() + "】";
                    System.out.println(parter + "开始执行……");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            Thread.sleep(2000);// 裁判准备发令
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        countDownLatch.countDown();// 发令枪：执行发令

    }

    public static void demo2() {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    Thread.sleep(1000 + ThreadLocalRandom.current().nextInt(1000));
                    System.out.println("finish" + index + Thread.currentThread().getName());
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            countDownLatch.await();// 主线程在阻塞，当计数器==0，就唤醒主线程往下执行。
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程:在所有任务运行完成后，进行结果汇总");
    }
}
