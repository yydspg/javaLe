package com.sky.context;

public class BaseContext{
    //此处必须使用 static 修饰,原因是static method 需要访问static 变量

    private  static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long id){threadLocal.set(id);}

    public static Long getCurrentId(){return threadLocal.get();}

    public static void removeCurrentId(){threadLocal.remove();}
}