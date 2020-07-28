package com.clubfactory.bargain.core.pxy;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class C {

    @Autowired
    D d;

    @Async
    @Transactional
    public void c1() {
        d.d1();
    }
}

@Service
class D {

    @Autowired
    C c;

    @Async
    @Transactional
    public void d1() {
        c.c1();
    }
}