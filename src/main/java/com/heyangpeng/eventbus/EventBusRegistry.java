package com.heyangpeng.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBusRegistry {

    private static final Logger logger = LoggerFactory.getLogger(EventBusRegistry.class);

    private static final Object lock = new Object();

    private final ConcurrentMap<Class<?>, CopyOnWriteArrayList<Subscriber>> subscribers = new ConcurrentHashMap<>();

    private final ConcurrentMap<Class<?>, Boolean> listenerCache = new ConcurrentHashMap<>();

    public void register(Object listener){

        Objects.requireNonNull(listener);
        Class<?> listenerClass = listener.getClass();

        if(checkListenerCached(listenerClass)) {
            logger.warn(String.format("%s : has already been registered!",listenerClass));
            return;
        }

        synchronized (lock) {

            if(!checkListenerCached(listenerClass)) {
                Map<Class<?>, List<Subscriber>> candidates = EventBusFinder.findAllSubscribers(listener);

                if(!candidates.isEmpty()){
                    candidates.forEach((eventType,subs) -> subscribers.computeIfAbsent(eventType,t -> new CopyOnWriteArrayList<>()).addAll(subs));
                    listenerCache.putIfAbsent(listenerClass, Boolean.TRUE);
                    logger.info(String.format("%s : register successfully!",listenerClass));
                }
            }else {
                logger.warn(String.format("%s : has already been registered!",listenerClass));
            }
        }
    }

    public void unregister(Object listener){

        Objects.requireNonNull(listener);
        Class<?> listenerClass = listener.getClass();

        if(!checkListenerCached(listenerClass)) {
            logger.warn(String.format("%s : was registered?",listenerClass));
            return;
        }

        Class<?> eventType;                                     //事件类型
        CopyOnWriteArrayList<Subscriber> currentSubscribers;    //事件类型对应的所有订阅者
        List<Subscriber> loseSubscribers;                       //落选的订阅者

        synchronized (lock) {

            if (checkListenerCached(listenerClass)) {

                Map<Class<?>, List<Subscriber>> listenerMethods = EventBusFinder.findAllSubscribers(listener);

                for (Map.Entry<Class<?>, List<Subscriber>> entry : listenerMethods.entrySet()) {

                    eventType = entry.getKey();
                    loseSubscribers = entry.getValue();
                    currentSubscribers = getSubscribers(eventType);

                    for(Subscriber loser : loseSubscribers){
                        currentSubscribers.removeIf(current -> current.getTarget().getClass() == loser.getTarget().getClass());
                    }
                }

                listenerCache.remove(listenerClass);
                logger.info(String.format("%s : unregistered!",listenerClass));
            }else {
                logger.warn(String.format("%s : was registered?",listenerClass));
            }
        }
    }

    /**
     * 检查订阅者是否已注册
     * @param listenerClass 监听类
     * @return true-已注册,false-未注册
     */
    private boolean checkListenerCached(Class<?> listenerClass) {
        Boolean cache = listenerCache.get(listenerClass);
        return cache != null && cache.equals(Boolean.TRUE);
    }

    /**
     * 从缓存获取事件对应的订阅者列表
     * @param object 事件类型
     */
    public CopyOnWriteArrayList<Subscriber> getSubscribers(Object object){

        CopyOnWriteArrayList<Subscriber> subscribersForType;
        Class<?> eventType;

        if(object instanceof Class){
            eventType = (Class<?>) object;
        }else {
            eventType = object.getClass();
        }

        subscribersForType = this.subscribers.get(eventType);

        return ObjectUtils.isEmpty(subscribersForType) ? null : subscribersForType;
    }

}
