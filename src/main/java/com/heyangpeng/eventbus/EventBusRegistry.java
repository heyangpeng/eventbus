package com.heyangpeng.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EventBusRegistry {

    public static final Logger logger = LoggerFactory.getLogger(EventBusRegistry.class);

    private final Map<Class<?>, List<Subscriber>> subscribers = new HashMap<>();

    private final Map<Class<?>, Boolean> subscribeCache = new HashMap<>();

    public void register(Object listener){

        if(Objects.isNull(listener)){
            throw new NullPointerException();
        }

        Class<?> listenerClass = listener.getClass();
        if(!Objects.isNull(subscribeCache.get(listenerClass))){
            logger.error(String.format("%s : has already been registered!",listenerClass));
            return;
        }

        Map<Class<?>, List<Subscriber>> candidates = EventBusFinder.findAllSubscribers(listener);
        if(!candidates.isEmpty()){
            subscribeCache.put(listenerClass, true);
            logger.info(String.format("%s : register successfully!",listenerClass));
            candidates.forEach((eventType,subs) -> subscribers.computeIfAbsent(eventType,t -> new ArrayList<>()).addAll(subs));
        }
    }

    public void unregister(Object subscriber){

    }

    public void dispatch(Object event){

    }


}
