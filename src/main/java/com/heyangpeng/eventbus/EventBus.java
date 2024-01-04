package com.heyangpeng.eventbus;

import com.heyangpeng.eventbus.common.ThreadType;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class EventBus {

    private static final EventBus instance = new EventBus();

    private static final EventBusRegistry registry = new EventBusRegistry();

    private static final ThreadFactory factory = new EventBusThreadFactory(1);

    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;

    private static final Executor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            3L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            factory
    );

    public void register(Object subscriber){
        registry.register(subscriber);
    }

    public void unregister(Object subscriber){
        registry.unregister(subscriber);
    }

    public void post(Object event){

        List<Subscriber> subscribersForType = prepareSubscribers(event);

        if(ObjectUtils.isEmpty(subscribersForType)){
            return;
        }

        subscribersForType.forEach(subscriber -> {
            if(subscriber.getThreadType() == ThreadType.ASYNC) {
                executor.execute(() -> subscriber.invoke(event));
            }else {
                subscriber.invoke(event);
            }
        });

    }

    /**
     * 根据事件类型获取订阅者列表
     * @param event 事件
     */
    protected List<Subscriber> prepareSubscribers(Object event) {
        if (Objects.isNull(event)) {
            return Collections.emptyList();
        }

        List<Subscriber> subscribersForType = registry.getSubscribers(event);
        if (ObjectUtils.isEmpty(subscribersForType)) {
            return Collections.emptyList();
        }

        return subscribersForType;
    }

    public static EventBus getInstance(){
        return instance;
    }

    final EventBusRegistry getRegistry() {
        return registry;
    }

    final Executor getExecutor() {
        return executor;
    }

}
