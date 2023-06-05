package com.yushijie.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockSupportDemo {

    private static final Object lock1 = new Object();

    public static void main(String[] args) {
//        demo1();
        demo2();
    }

    /**
     * wait() notify()
     * 存在的问题
     * 1. 必须在同一把锁的同步代码中
     * 2. 如果notify()在wait()之前执行，就会“丢失唤醒”
     */
    private static void demo1() {
        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                try {
                    System.out.println("t1 要睡了");
                    lock1.wait();
                    System.out.println("t1 被唤醒");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (lock1) {
                try {
                    Thread.sleep(3000L);
                    lock1.notify();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t2");
        t2.start();
    }

    /**
     * Condition await() signal() 也是必须在锁块中使用 且signal()先调用也会丢失唤醒
     */
    public static void demo2() {
        ReentrantLock lock = new ReentrantLock();//默认非公平锁
        Condition condition = lock.newCondition();

        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("t1要睡了");
                condition.await();
                System.out.println("t1被唤醒");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            lock.lock();
            System.out.println("唤醒t1");
            condition.signal();
            lock.unlock();
        }, "t2");
        t2.start();

    }
}
