package util;

public class Timer {

    private long start;

    public Timer() {
        start = System.nanoTime();
    }

    public long diff() {
        return (System.nanoTime() - start) / 1000;
    }
}
