### EventBus

------
EventBus是发布-订阅模型的实现，具有低耦合的组件通信特性，让代码更简洁、易维护。

### 环境要求

------
- JDK1.8+
### 开始使用

------
1. 订阅事件：所有Object类都可作为事件，在监听类中创建监听事件的方法，并使用`@Subscribe`标记在方法上，`threadType`属性决定是同步还是异步处理事件（默认是同步)。

```java
@Subscribe(threadType = ThreadType.SYNC)
public void receive(String message){
    // do something
}
```
2. 注册并发布事件：提供`EventBus`和`AsyncEventBus`两种事件总线的实现，`EventBus`提供灵活的处理方式，`AsyncEventBus`中所有事件都是异步处理的。
```java
public static void main(String[] args){
    EventBus eventbus = EventBus.getInstance();
    // 注册
    eventbus.register(this);
    // 发布
    eventbus.post("anything");
    // 注销
    eventbus.unregister(this);
}
```
`AsyncEventBus`同上。

本项目采用单例模式，可在任何位置使用`EventBus.getInstance()`获取总线单例，另外，注册与注销具有线程安全性。



