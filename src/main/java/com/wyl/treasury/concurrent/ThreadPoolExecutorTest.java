package com.wyl.treasury.concurrent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolExecutorTest {
    public static class MyCallable implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println(new Date() + " Thread Id:" + Thread.currentThread().getId() + " Thread Name:" + Thread.currentThread().getName());
            Thread.sleep(3000);
            return null;
        }

    }

    public static void main(String[] args) {
        ThreadPoolExecutor tpe = new ThreadPoolExecutor(2, 5, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1));
        System.out.println("1:" + tpe);
        Future<Integer> f = tpe.submit(new MyCallable());
        System.out.println("2:" + tpe);
        try {
            System.out.println(f.get());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("3:" + tpe);
        f = tpe.submit(new MyCallable());
        System.out.println("4:" + tpe);
        try {
            System.out.println(f.get());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("5:" + tpe);

        List<Callable<Integer>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new MyCallable());
        }
        try {
            List<Future<Integer>> futures = tpe.invokeAll(list);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("6:" + tpe);
        tpe.shutdown();
//        tpe.submit(new MyCallable());
//        tpe.shutdown();
    }
}
