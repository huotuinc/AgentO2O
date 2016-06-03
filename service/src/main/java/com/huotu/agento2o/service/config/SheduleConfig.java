package com.huotu.agento2o.service.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * Created by elvis on 2016/5/16.
 */
@Configuration
@EnableAsync
//@EnableScheduling
public class SheduleConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
//        ExecutorService executor = Executors.newCachedThreadPool();
//if(executor instanceof ThreadPoolTaskExecutor){
//    ((ThreadPoolTaskExecutor) executor).setCorePoolSize(7);
//
//}

//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(7);
//        executor.setMaxPoolSize(15);
//        /*executor.setQueueCapacity(100);*/
//        executor.setThreadNamePrefix("eamilThread-");
//        executor.initialize();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(5,12,3,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(500),
                new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
