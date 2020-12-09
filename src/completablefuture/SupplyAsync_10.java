package completablefuture;

import org.junit.Test;
import util.Nap;
import util.Timer;
import util._Print;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static util._Print.println;

public class SupplyAsync_10 {

    @Test
    public void supplyTest() throws IOException {
        Timer timer = new Timer();
        println("****** start ******");
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(SupplyAsync_10::fA);
        new Nap(1500);
        cf.thenApply(SupplyAsync_10::fB);
        cf.thenApply(SupplyAsync_10::fC);
        println(timer.duration());
        println("****** end ******");
    }

    @Test
    public void supplyAsyncTest() throws IOException {
        Timer timer = new Timer();
        println("****** start ******");
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(SupplyAsync_10::fA);
        new Nap(10);
        cf.thenApplyAsync(SupplyAsync_10::fB);
        cf.thenApplyAsync(SupplyAsync_10::fC);
        println(timer.duration());
        println("****** end ******");
        System.in.read();
    }

    private static String fA() {
        new Nap(1000,"fA");
        return "hello";
    }

    private static String fB(String str) {
        new Nap(1500,"fB");
        return str + " world";
    }

    private static String fC(String str) {
        new Nap(1000,"fC");

        return str + " world";
    }

}
