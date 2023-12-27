package com.heyangpeng.eventbus;

import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;

public class EventBus {

    private static final EventBus instance = new EventBus();

    private final EventBusRegistry registry = new EventBusRegistry();

    public void register(Object subscriber){
        this.registry.register(subscriber);
    }

    public void unregister(Object subscriber){
        this.registry.unregister(subscriber);
    }

    public void post(Object event){

        if(Objects.isNull(event)) {
            return;
        }

        List<Subscriber> subscribersForType = this.registry.getSubscribers(event);

        if(ObjectUtils.isEmpty(subscribersForType)){
            return;
        }

        subscribersForType.forEach(subscriber -> subscriber.invoke(event));

    }

    public static EventBus getInstance(){
        return instance;
    }

}
