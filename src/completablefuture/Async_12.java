package completablefuture;

import util.Nap;

import java.util.concurrent.CompletableFuture;

import static util._Print.println;

public class Async_12 {

    public static void main(String[] args) throws Exception {
        //thenApply和thenApplyAsync的区别
        System.out.println("-------------");
        CompletableFuture<String> supplyAsyncWithSleep = CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "supplyAsyncWithSleep Thread Id : " + Thread.currentThread();
        });
        CompletableFuture<String> thenApply = supplyAsyncWithSleep
                .thenApply(name -> name + "------thenApply Thread Id : " + Thread.currentThread());
        CompletableFuture<String> thenApplyAsync = supplyAsyncWithSleep
                .thenApplyAsync(name -> name + "------thenApplyAsync Thread Id : " + Thread.currentThread());
        System.out.println("Main Thread Id: "+ Thread.currentThread());
        System.out.println(thenApply.get());
        System.out.println(thenApplyAsync.get());
        System.out.println("-------------No Sleep");
        CompletableFuture<String> supplyAsyncNoSleep = CompletableFuture.supplyAsync(()->{
            return "supplyAsyncNoSleep Thread Id : " + Thread.currentThread();
        });
        CompletableFuture<String> thenApplyNoSleep = supplyAsyncNoSleep
                .thenApply(name -> name + "------thenApply Thread Id : " + Thread.currentThread());
        CompletableFuture<String> thenApplyAsyncNoSleep = supplyAsyncNoSleep
                .thenApplyAsync(name -> name + "------thenApplyAsync Thread Id : " + Thread.currentThread());
        System.out.println("Main Thread Id: "+ Thread.currentThread());
        System.out.println(thenApplyNoSleep.get());
        System.out.println(thenApplyAsyncNoSleep.get());

    }

}

