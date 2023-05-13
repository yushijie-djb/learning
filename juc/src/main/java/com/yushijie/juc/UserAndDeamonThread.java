package com.yushijie.juc;

public class UserAndDeamonThread {
    public static void main(String[] args) throws InterruptedException {
        /*
        //当前线程为用户线程时 jvm永远不会退出
        Thread t = new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getId() + (Thread.currentThread().isDaemon() ? "守护线程" : "用户线程"));
                while (true) {

                }
            }
        };
        t.start();
        */
        //当前线程为守护线程时 由于没有用户线程 JVM会在main线程sleep 3秒后退出
        Thread t = new Thread(){
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getId() + (Thread.currentThread().isDaemon() ? "守护线程" : "用户线程"));
                while (true) {

                }
            }
        };
        t.setDaemon(true);
        t.start();

        Thread.sleep(3000L);
    }
}
