package com.huotu.agento2o.service.service.product.impl;

import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by elvis on 2016/5/20.
 */
@Service
public class BlockExecutor {


        class Task implements Runnable {
            private String name;
            public Task(String name) {
                this.name = name;
            }
            public void run() {
                System.out.println("the "+ name + " task");
                try {
                    //模拟线程需要执行的时间
                    Thread.sleep(2000);
                    System.out.println("Thread Name is "+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void execute() throws InterruptedException {
            ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3,6,3, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),new ThreadPoolExecutor.CallerRunsPolicy());

            for(int i = 1; i<=50;i++) {
                System.out.println("put in executor"+i);
                Task task = new Task(""+i);
                threadPool.execute(task);
            }
            threadPool.shutdown();
            while(true){
                if(threadPool.isTerminated()){
                    System.out.println("所有的子线程都结束了！");
                    break;
                }
                Thread.sleep(1000);
            }
        }

        public static void main(String[] args) throws InterruptedException {
            BlockExecutor t = new BlockExecutor();
            t.execute();
        }
    }
