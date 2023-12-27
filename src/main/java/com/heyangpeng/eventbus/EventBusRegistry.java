package com.heyangpeng.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

public class EventBusRegistry {

    public static final Logger logger = LoggerFactory.getLogger(EventBusRegistry.class);

    private final Map<Class<?>, List<Subscriber>> subscribers = new HashMap<>();

    private final Map<Class<?>, Boolean> listenerCache = new HashMap<>();

    public void register(Object listener){

        if(Objects.isNull(listener)){
            throw new NullPointerException();
        }

        Class<?> listenerClass = listener.getClass();
        if(!Objects.isNull(listenerCache.get(listenerClass))){
            logger.warn(String.format("%s : has already been registered!",listenerClass));
            return;
        }

        Map<Class<?>, List<Subscriber>> candidates = EventBusFinder.findAllSubscribers(listener);
        if(!candidates.isEmpty()){
            listenerCache.put(listenerClass, true);
            logger.info(String.format("%s : register successfully!",listenerClass));
            candidates.forEach((eventType,subs) -> subscribers.computeIfAbsent(eventType,t -> new ArrayList<>()).addAll(subs));
        }
    }

    public void unregister(Object listener){
        if(Objects.isNull(listener)){
            throw new NullPointerException();
        }

        Class<?> listenerClass = listener.getClass();
        Boolean listenerExisted = listenerCache.get(listenerClass);
        
        if(Objects.isNull(listenerExisted)) {
            logger.warn(String.format("%s : was registered?",listenerClass));
            return;
        }

        Map<Class<?>, List<Subscriber>> listenerMethods = EventBusFinder.findAllSubscribers(listener);

        Class<?> eventType;                     //事件类型
        List<Subscriber> currentSubscribers;    //事件类型对应的所有订阅者
        List<Subscriber> loseSubscribers;       //落选的订阅者

        if(!ObjectUtils.isEmpty(listenerMethods)){

            for (Map.Entry<Class<?>, List<Subscriber>> entry : listenerMethods.entrySet()) {
                eventType = entry.getKey();
                loseSubscribers = entry.getValue();
                currentSubscribers = getSubscribers(eventType);

                for(Subscriber loser : loseSubscribers){
                    Iterator<Subscriber> iterator = currentSubscribers.iterator();

                    while(iterator.hasNext()){
                        Subscriber current = iterator.next();
                        if(current.getTarget().getClass() == loser.getTarget().getClass()){
                            iterator.remove();
                        }
                    }
                }
            }
            listenerCache.remove(listenerClass);
            logger.info(String.format("%s : unregistered!",listenerClass));
        }
    }

    public List<Subscriber> getSubscribers(Object object){

        List<Subscriber> subscribersForType;
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
