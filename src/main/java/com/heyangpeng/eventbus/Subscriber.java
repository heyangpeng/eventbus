package com.heyangpeng.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Subscriber {

    private final Object target;
    private final Method method;

    static Subscriber create(Object listener,Method method){
        return new Subscriber(listener, method);
    }

    private Subscriber(Object listener,Method method){
        this.target = listener;
        this.method = method;
    }

    private void invoke(final Object event){
        try{
            this.method.invoke(this.target, event);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Method getMethod() {
        return this.method;
    }

    public Object getTarget() {
        return this.target;
    }
}
