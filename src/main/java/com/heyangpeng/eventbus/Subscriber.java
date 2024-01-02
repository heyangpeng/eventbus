package com.heyangpeng.eventbus;

import com.heyangpeng.eventbus.common.ThreadType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Subscriber {

    private final Object target;
    private final Method method;
    private final ThreadType threadType;

    static Subscriber create(Object listener, Method method, ThreadType threadType){
        return new Subscriber(listener, method, threadType);
    }

    private Subscriber(Object listener, Method method, ThreadType threadType){
        this.target = listener;
        this.method = method;
        this.threadType = threadType;
    }

    public void invoke(final Object event) {
        try{
            this.method.invoke(this.target, event);
        } catch (IllegalArgumentException var1) {
            throw new Error(this.method + " rejected argument: " + event.getClass());
        } catch (IllegalAccessException var2) {
            throw new Error(this.method + " was inaccessible");
        } catch (InvocationTargetException var3) {
            throw new Error(this.method + " invoke failed");
        }
    }

    public Method getMethod() {
        return this.method;
    }

    public Object getTarget() {
        return this.target;
    }

    public ThreadType getThreadType() {
        return this.threadType;
    }
}
