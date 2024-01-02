package com.heyangpeng.eventbus;

import org.springframework.util.ObjectUtils;

import java.util.List;

public class AsyncEventBus extends EventBus{

    private static final AsyncEventBus instance = new AsyncEventBus();

    @Override
    public void post(Object event){

        List<Subscriber> subscribersForType = prepareSubscribers(event);

        if(ObjectUtils.isEmpty(subscribersForType)){
            return;
        }

        subscribersForType.forEach(subscriber -> getExecutor().execute(() -> subscriber.invoke(event)));

    }

    public static EventBus getInstance() {
        return instance;
    }
}
