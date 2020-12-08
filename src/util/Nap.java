package util;

import java.util.concurrent.TimeUnit;

public class Nap {
    public Nap(long t) {
        try {
            TimeUnit.SECONDS.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
