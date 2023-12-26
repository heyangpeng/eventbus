package com.heyangpeng.eventbus;

public class EventBus {

    private static final EventBus instance = new EventBus();

    private final EventBusRegistry registry = new EventBusRegistry();

    public void register(Object subscriber){
        this.registry.register(subscriber);
    }

    public void unregister(Object subscriber){

    }

    public void post(Object event){

    }

    public static EventBus getInstance(){
        return instance;
    }

}
