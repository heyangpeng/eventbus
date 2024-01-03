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

    private final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    private final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            3L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            Executors.defaultThreadFactory()
    );

    @Test
    public void testMultiThreadRegister() {

        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            tasks.add(() -> asyncEventBus.register(this));
        }

        // 提交所有任务
        tasks.forEach(executor::execute);
        executor.shutdown();

        asyncEventBus.post("Hello World!");
    }

    @Test
    public void testMultiThreadUnregister() {

        List<Runnable> tasks = new ArrayList<>();
        asyncEventBus.register(this);

        for (int i = 0; i < 100; i++) {
            tasks.add(() -> asyncEventBus.unregister(this));
        }

        // 提交所有任务
        tasks.forEach(executor::execute);
        executor.shutdown();

        asyncEventBus.post("Hello World!");
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
