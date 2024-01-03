package com.heyangpeng.eventbus;

import com.heyangpeng.eventbus.annotation.Subscribe;
import com.heyangpeng.eventbus.common.ThreadType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EventBusBasicTests {

    public EventBus eventBus = EventBus.getInstance();

    public EventBus asyncEventBus = AsyncEventBus.getInstance();

    public static final Logger logger = LoggerFactory.getLogger(EventBusBasicTests.class);

    @Test
    public void testBasic() {
        // 同步
        eventBus.register(this);
        eventBus.post("hello world!");
        eventBus.post(100);
        eventBus.unregister(this);

        // 异步
        asyncEventBus.register(this);
        asyncEventBus.post("hello world!");
        asyncEventBus.post(100);
        asyncEventBus.unregister(this);
    }

    @Test
    public void testRegisterTwice() {
        // 同步
        eventBus.register(this);
        eventBus.register(this);

        // 异步
        asyncEventBus.register(this);
        asyncEventBus.register(this);
    }

    @Test
    public void testUnregisterTwice() {

        // 同步
        eventBus.register(this);
        eventBus.unregister(this);
        eventBus.unregister(this);

        // 异步
        asyncEventBus.register(this);
        asyncEventBus.unregister(this);
        asyncEventBus.unregister(this);
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
