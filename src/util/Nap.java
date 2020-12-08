package util;

import java.util.concurrent.TimeUnit;

import static util._Print.println;

public class Nap {
    public Nap(long t) {
        try {
            TimeUnit.SECONDS.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void doSomeThing() {
        new Nap(6);
        println("doSomeThing 等待异步接口的同时，我可以做一些其他的事确 花费了3s时间");
    }
}
