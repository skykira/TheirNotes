package com.clubfactory.bargain.server;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.cglib.core.DebuggingClassWriter;

import java.lang.reflect.Method;

public class LogInterceptor{

    public static void main(String[] args) {
        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,".");
        HelloImpl hello = new HelloImpl();
        ProxyFactory proxyFactory = new ProxyFactory(hello);
        MyBeforeAdvice myBeforeAdvice = new MyBeforeAdvice();
        MyMethodInterceptor myMethodInterceptor = new MyMethodInterceptor();
        proxyFactory.addAdvice(myBeforeAdvice);
        proxyFactory.addAdvice(myMethodInterceptor);
        HelloImpl proxy = (HelloImpl) proxyFactory.getProxy();

        proxy.hi("hello A!");
    }
}

class MyBeforeAdvice implements MethodBeforeAdvice {

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("before method do sth.");
    }
}

class MyMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("start this method.");
        return invocation.proceed();
    }
}