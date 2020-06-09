package com.clubfactory.bargain.server;

public class HelloImpl implements Hello {
    @Override
    public void hi(String msg) {
        System.out.println("子类："+msg);
    }
}
