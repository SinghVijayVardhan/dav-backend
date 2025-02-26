package org.dav.utils;

public class CurrentThread {
    private static ThreadLocal<String> email = new ThreadLocal<String>();
    private static ThreadLocal<Integer> id = new ThreadLocal<Integer>();

    public static void setEmail(String email){
        CurrentThread.email.set(email);
    }

    public static void setId(Integer id){
        CurrentThread.id.set(id);
    }

    public static String getEmail(){
        return email.get();
    }

    public static Integer getId(){
        return id.get();
    }
}
