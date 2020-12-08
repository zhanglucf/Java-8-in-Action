package completablefuture;

import org.junit.Test;
import util.Nap;
import util.Timer;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static util._Print.println;

public class _Future {

    //推荐使用Future带有超时参数的get方法
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        demo();
//        demo2();
//        demo3();
//        double price = getPrice();
//        println(price);
    }

    /**
     * 下面代码演示在Java8之前，如何通过Future实现异步任务，并获取异步任务结果的
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void demo() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<String> submit = es.submit(() -> {
            println("异步任务执行开始");
            new Nap(3);
            return "异步任务执行结果";
        });
        doSomeThing();
        try {
            println(submit.get());
        } catch (InterruptedException e) {
            println("异步任务被打断");
            e.printStackTrace();
        } catch (ExecutionException e) {
            println("异步任务内部抛出异常");
            e.printStackTrace();
        }
        println("主线程结束");
        es.shutdown();
    }

    /**
     * 下面代码演示在Java8之前，如何通过Future实现异步任务，并获取异步任务结果的
     * 在异步任务中主动抛出RuntimeException异常,
     * 这个异常会被转化成ExecutionException，并被try代码块捕获
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void demo2() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<String> submit = es.submit(() -> {
            println("异步任务执行开始");
            new Nap(3);
            throw new RuntimeException("@@@");
//            return "异步任务执行结果";
        });
        doSomeThing();
        try {
            //超多指定的时间，如果异步任务没有返回结果，则会抛出TimeoutException
            println(submit.get(2, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            println("异步任务被打断");
            e.printStackTrace();
        } catch (ExecutionException e) {
            println("异步任务内部抛出异常");
            e.printStackTrace();
        } catch (TimeoutException e) {
            println("异步任务在指定的时间没没有应答");
            e.printStackTrace();
        }
        println("主线程结束");
        es.shutdown();
    }

    /**
     * 下面代码演示在Java8之前，如何通过Future实现异步任务，并获取异步任务结果的
     * 设置异步任务阻塞时间
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void demo3() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<String> submit = es.submit(() -> {
            println("异步任务执行开始");
            new Nap(6);
            return "异步任务执行结果";
        });
        doSomeThing();
        try {
            //超多指定的时间，如果异步任务没有返回结果，则会抛出TimeoutException
            println(submit.get(2, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            println("异步任务被打断");
            e.printStackTrace();
        } catch (ExecutionException e) {
            println("异步任务内部抛出异常");
            e.printStackTrace();
        } catch (TimeoutException e) {
            println("异步任务在指定的时间没没有应答");
            e.printStackTrace();
        }
        println("主线程结束");
        es.shutdown();
    }

    @Test
    public void getPrice() {
        Timer timer = new Timer();
        List<String> shopNames = Arrays.asList("京东", "淘宝", "拼多多");
        List<Future<Double>> fs = new ArrayList<>();
        for (String shopName : shopNames) {
            Future<Double> f = getPriceAsyncByShopName(shopName);
            fs.add(f);
        }

        doSomeThing();
        println(timer.diff());

        Double price = fs.stream()
                .map(f -> {
                    try {
                        return f.get(30, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.maxBy(Double::compare))
                .orElse(-1.0);
        println(price);
        println(timer.diff());
    }

    private static Future<Double> getPriceAsyncByShopName(String shopName) {
        CompletableFuture<Double> cf = new CompletableFuture<>();
        switch (shopName) {
            case "京东":
                new Thread(() -> {
                    println("调用京东商品价格API");
                    println(" calling 京东API");
                    new Nap(3);
                    println(" calling 京东API successful");

                    cf.complete(300.0);
                }).start();
                break;
            case "淘宝":
                new Thread(() -> {
                    println("调用淘宝商品价格API");
                    println(" calling 淘宝API");
                    new Nap(2);
                    println(" calling 淘宝API successful");

                    cf.complete(200.0);
                }).start();
                break;
            case "拼多多":
                new Thread(() -> {
                    println("调用拼多多商品价格API");
                    println(" calling 拼多多API");
                    new Nap(5);
                    println(" calling 拼多多API successful");

                    cf.complete(100.0);
                }).start();
                break;
        }
        return cf;
    }

    private static void doSomeThing() {
        new Nap(3);
        System.out.println("doSomeThing 等待异步接口的同时，我可以做一些其他的事确 花费了3s时间");
    }
}
