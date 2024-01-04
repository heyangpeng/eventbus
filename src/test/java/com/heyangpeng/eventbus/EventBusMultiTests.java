package com.heyangpeng.eventbus;

import com.heyangpeng.eventbus.annotation.Subscribe;
import com.heyangpeng.eventbus.common.ThreadType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class EventBusMultiTests {

    public EventBus asyncEventBus = AsyncEventBus.getInstance();

    public static final Logger logger = LoggerFactory.getLogger(EventBusBasicTests.class);

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) asyncEventBus.getExecutor();

    @Test
    public void testMultiThreadRegister() {

        List<Callable<Object>> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            tasks.add(Executors.callable(() -> asyncEventBus.register(this)));
        }

        try {
            // 提交所有任务
            executor.invokeAll(tasks);
            // 发布事件
            asyncEventBus.post("Hello World!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭Executor
            executor.shutdown();
        }
    }

    @Test
    public void testMultiThreadUnregister() {

        List<Callable<Object>> tasks = new ArrayList<>();
        asyncEventBus.register(this);

        for (int i = 0; i < 100; i++) {
            tasks.add(Executors.callable(() -> asyncEventBus.unregister(this)));
        }

        try {
            // 提交所有任务
            executor.invokeAll(tasks);
            // 发布事件
            asyncEventBus.post("Hello World!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭Executor
            executor.shutdown();
        }
    }

    @Subscribe
    public void testStringEvent(String event) {
        logger.info("testStringEvent: " + event);
    }

    @Subscribe(threadType = ThreadType.ASYNC)
    public void testIntegerEvent(Integer event) {
        logger.info("testIntegerEvent: " + event);
    }

}
