package util;

public class _Print {

    public static void println(Object obj){
        System.out.println(Thread.currentThread().getName()+" "+obj);
    }
}
