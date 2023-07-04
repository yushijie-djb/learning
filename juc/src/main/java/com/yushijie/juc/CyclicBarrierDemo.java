package com.yushijie.juc;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2023/7/3 11:02
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        CyclicBarrier barrier = new CyclicBarrier(5, new Runnable() {
            @Override
            public void run() {
                System.out.println("全部到达"+Thread.currentThread().getName()+"呼叫服务员开始点餐！");
                service.shutdown();
            }
        });
        for (int j = 0; j < 5; j++) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(new Random().nextInt(3000));
                        System.out.println(Thread.currentThread().getName() + "同学到达");
                        barrier.await();
                        System.out.println(Thread.currentThread().getName()+"同学点餐");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }

            });
        }

        service.shutdown();
    }


}
