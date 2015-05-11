package com.leya.idal.model;

public class ExecutionContext {

    private static ThreadLocal<Object> context = new ThreadLocal<Object>();

    public static void setContext(Object value) {
        context.set(value);
    }

    public static Object getContext() {
        return context.get();
    }

    public static void removeContext() {
        context.remove();
    }

}
