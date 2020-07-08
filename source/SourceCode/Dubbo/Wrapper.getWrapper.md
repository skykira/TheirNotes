### 接口

```java
interface DubboTest {
    Integer num = 1;

    Integer getNum();

    void setNum(Integer num);

    String sayHello();

    String getStr();

    void setStr(String s);
}
```

### dubbo Wrapper.getWrapper(DubboTest.class) 生成的类部分结构

```java
C1
    public void setPropertyValue(Object o, String n, Object v) {
        com.clubfactory.bargain.server.DubboTest w;
        try {
            w = ((com.clubfactory.bargain.server.DubboTest) $1);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        if ($2.equals("num")) {
            w.setNum((java.lang.Integer) $3);
            return;
        }
        if ($2.equals("str")) {
            w.setStr((java.lang.String) $3);
            return;
        }
        throw new com.alibaba.dubbo.common.bytecode.NoSuchPropertyException("Not found property \"" + $2 + "\" filed or setter method in class com.clubfactory.bargain.server.DubboTest.");
    }


C2

    public Object getPropertyValue(Object o, String n) {
        com.clubfactory.bargain.server.DubboTest w;
        try {
            w = ((com.clubfactory.bargain.server.DubboTest) $1);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        if ($2.equals("str")) {
            return ($w) w.getStr();
        }
        if ($2.equals("num")) {
            return ($w) w.getNum();
        }
        throw new com.alibaba.dubbo.common.bytecode.NoSuchPropertyException("Not found property \"" + $2 + "\" filed or setter method in class com.clubfactory.bargain.server.DubboTest.");
    }

C3

    public Object invokeMethod(Object o, String n, Class[] p, Object[] v) throws java.lang.reflect.InvocationTargetException {
        com.clubfactory.bargain.server.DubboTest w;
        try {
            w = ((com.clubfactory.bargain.server.DubboTest) $1);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
        try {
            if ("setNum".equals($2) && $3.length == 1) {
                w.setNum((java.lang.Integer) $4[0]);
                return null;
            }
            if ("setStr".equals($2) && $3.length == 1) {
                w.setStr((java.lang.String) $4[0]);
                return null;
            }
            if ("getStr".equals($2) && $3.length == 0) {
                return ($w) w.getStr();
            }
            if ("sayHello".equals($2) && $3.length == 0) {
                return ($w) w.sayHello();
            }
            if ("getNum".equals($2) && $3.length == 0) {
                return ($w) w.getNum();
            }
        } catch (Throwable e) {
            throw new java.lang.reflect.InvocationTargetException(e);
        }
        throw new com.alibaba.dubbo.common.bytecode.NoSuchMethodException("Not found method \"" + $2 + "\" in class com.clubfactory.bargain.server.DubboTest.");
    }
```