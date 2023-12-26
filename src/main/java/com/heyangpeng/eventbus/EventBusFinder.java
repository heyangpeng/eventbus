package com.heyangpeng.eventbus;

import com.heyangpeng.eventbus.annotation.Subscribe;

import java.lang.reflect.Method;
import java.util.*;

public class EventBusFinder {

    /**
     * 找到所有订阅者
     * @param listener 监听事件的对象
     * @return 事件到订阅者的映射
     */
    public static Map<Class<?>, List<Subscriber>> findAllSubscribers(final Object listener){
        Map<Class<?>, List<Subscriber>> annotatedMethods = new HashMap<>();
        Method[] methods = listener.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                checkParameterAllowed(method);
                Class<?> parameterType = method.getParameterTypes()[0];
                annotatedMethods.computeIfAbsent(parameterType, k -> new ArrayList<>()).add(Subscriber.create(listener, method));
            }
        }

        return annotatedMethods;
    }

    private static void checkParameterAllowed(final Method method){
        if(method.getParameterCount() != 1){
            throw new IllegalArgumentException(String.format("Subscriber %s must have exactly 1 parameter",method));
        }
    }

}
