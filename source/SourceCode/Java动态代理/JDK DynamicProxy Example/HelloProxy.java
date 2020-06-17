package com.clubfactory.bargain.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HelloProxy implements InvocationHandler {
    public static void main(String[] args) {
        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        Hello hello = (Hello) Proxy.newProxyInstance(Hello.class.getClassLoader(), new Class[]{Hello.class}, new HelloProxy(new HelloImpl()));

        hello.hi("信息");

        System.out.println(hello.toString());

        System.out.println(hello.hashCode());

        System.out.println("结束");
    }

    private Object target;

    public HelloProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("proxy invoke &&" + method.getName());
        return method.invoke(target, args);
    }
}
