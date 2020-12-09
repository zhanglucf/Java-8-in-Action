package util;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static util._Print.println;

public class Nap {
    private static final Random RANDOM = new Random();

    public Nap(long t) {
        try {
            TimeUnit.MILLISECONDS.sleep(t);
            println(String.format("sleep %dms",t ));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Nap(long t,String str) {
        try {
            TimeUnit.MILLISECONDS.sleep(t);
            println(String.format(" %s sleep %dms",  str,t ));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void sleepRandom() {
        int delay = 50 + RANDOM.nextInt(3000);
        new Nap(delay);
//        println("随机sleep " + delay );
    }

    public static void doSomeThing() {
        new Nap(10);
        println("doSomeThing 等待异步接口的同时，我可以做一些其他的事确 花费了3s时间");
    }
}
