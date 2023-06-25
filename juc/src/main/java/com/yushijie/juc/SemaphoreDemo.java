package com.yushijie.juc;

import java.util.concurrent.*;

/**
 * @author yushijie
 * @version 1.0
 * @description
 * @date 2023/6/25 11:42
 */
public class SemaphoreDemo {
    private static Semaphore semaphore = new Semaphore(1);//只有一个许可

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        for (int i = 0; i < 5; i++) {
            threadPoolExecutor.execute(() -> {
                boolean b = semaphore.tryAcquire();
                if (b) {
                    System.out.println("Current Thread " + Thread.currentThread().getName() + "acquire success");
                    semaphore.release();
                } else {
                    System.out.println("Current Thread " + Thread.currentThread().getName() + "acquire fail");
                }
            });
        }

        threadPoolExecutor.shutdown();
    }
}
