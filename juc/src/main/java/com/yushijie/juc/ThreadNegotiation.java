package com.yushijie.juc;

/**
 * 线程中断协商机制
 */
public class ThreadNegotiation {
    static volatile boolean alive = false;
    public static void main(String[] args) {
        try {
//            demo1();
            demo2();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 基于volatile关键字实现
     */
    public static void demo1() throws InterruptedException {
        new Thread(() -> {
            while (true) {
                if (alive) {
                    System.out.println("alive");
                    break;
                }
                System.out.println("sleep");
            }
        }).start();

        Thread.sleep(1L);

        new Thread(() -> {
            alive = true;
        }).start();
    }

    /**
     * 基于interrupted
     */
    public static void demo2() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("interrupted");
                    break;
                }
                System.out.println("sleep");
            }
        });
        thread1.start();

        Thread.sleep(1L);

        Thread thread2 = new Thread(() -> {
            thread1.interrupt();
        });
        thread2.start();
    }

}
