package completablefuture;

import org.junit.Test;
import util.Nap;
import util.Timer;
import util._Print;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static util._Print.println;

/**
 * 测试类主要是用来说明CompletableFuture类中xxxAsync与xxx方法的区别
 *
 * 结论：
 *  以thenApply()与thenApplyAsync()为例
 *      1. 主线程执行到CF.thenApply(),
 *          ① 如果CF上一个异步任务在当前时间点没有完成，等到上个异步任务结束，thenApply中要执行的异步任务由
 *          默认线程池（ForkJoinPool）中的任意空闲线程执行。
 *          ② 如果CF上一个异步任务在当前时间点已经完成，thenApply中要执行的异步任务由主线程执行。
 *       2. 主线程执行到CF.thenApplyAsync(),等到CF对应的异步任务执行结束，使用默认线程池（ForkJoinPool）
 *          中的空闲线程执行thenApplyAsync()中要执行的任务。此外thenApplyAsync的重载版本可以接受指定执行器去
 *          执行当前任务
 *       3. 个人认为，二者主要区别在于哪个线程来执行异步任务，大多数的场景，对这个没有太大的区分，建议使用CF.thenApplyAsync(),
 *          我们能确定起的是异步任务，一定不会阻塞主线程。
 */
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
