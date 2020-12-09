package completablefuture;

import org.junit.Test;
import util.Nap;
import util.Timer;
import util._Print;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static util.Nap.doSomeThing;
import static util._Print.println;

/**
 *
 */
public class Price_5 {
    private static final List<String> SHOPS = Arrays.asList("京东", "淘宝", "拼多多");


    @Test
    public void getPrice() {
//        getAllShopPrice();
//        getAllShopPriceByParallelStream();

//        getAllShopPriceByCompletableFuture_v1();
        getAllShopPriceByCompletableFuture_v2();
    }



    /**
     * 使用异步接口实现
     * 使用了两条不同的Stream流水线，这样的好处是：
     * 第一条流水线先分配任务，异步线程可以先开始工作
     * 第二条流水线阻塞主线程，等待异步接口给出结果
     * 性能的瓶颈在于所有接口中最慢的那个
     * @return
     */
    private static List<String> getAllShopPriceByCompletableFuture_v2(){
        println("parallelStream");
        Timer timer = new Timer();
        List<Future<Double>> futureList = SHOPS.stream()
                .map(Price_5::getPriceAsyncByShopNameAsync)
                .collect(Collectors.toList());

        List<String> prices = futureList.stream()
                .map(f -> {
                    Double price = null;
                    try {
                        price = f.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return "" + price;
                }).collect(Collectors.toList());
        println("查询所有商点 关于 雅阁的价格 一共耗时" + timer.duration());
        println("各个商店雅阁的价格分别为：");
        prices.forEach(_Print::println);
        return prices;
    }

    /**
     * 使用异步接口实现(没有发挥异步接口的优势)
     * 每个接口时顺序调用的，一个接口有结果了才会调用下一个接口
     * 实际上和常规同步接口实现的性能没有太大差别
     * @return
     */
    private static List<String> getAllShopPriceByCompletableFuture_v1(){
        println("parallelStream");
        Timer timer = new Timer();
        List<String> prices = SHOPS.stream()
                .map(Price_5::getPriceAsyncByShopNameAsync)
                .map(f -> {
                    Double price = null;
                    try {
                        price = f.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return "" + price;
                })
                .collect(Collectors.toList());
        println("查询所有商点 关于 雅阁的价格 一共耗时" + timer.duration());
        println("各个商店雅阁的价格分别为：");
        prices.forEach(_Print::println);
        return prices;
    }


    /**
     * 并行流实现方式
     * @return
     */
    private static List<String> getAllShopPriceByParallelStream(){
        println("parallelStream");
        Timer timer = new Timer();
        List<String> prices = SHOPS.parallelStream()
                .map(x -> {
                    return x + "的价格是:" + getPriceAsyncByShopName(x);
                }).collect(Collectors.toList());
        println("查询所有商点 关于 雅阁的价格 一共耗时" + timer.duration());
        println("各个商店雅阁的价格分别为：");
        prices.forEach(_Print::println);
        return prices;
    }


    /**
     * 常规同步接口实现
     * @return
     */
    private static List<String> getAllShopPrice(){
        Timer timer = new Timer();
        List<String> prices = SHOPS.stream()
                .map(x -> {
                    return x + "的价格是:" + getPriceAsyncByShopName(x);
                }).collect(Collectors.toList());
        println("查询所有商点 关于 雅阁的价格 一共耗时" + timer.duration());
        println("各个商店雅阁的价格分别为：");
        prices.forEach(_Print::println);
        return prices;
    }

    private static Double getPriceAsyncByShopName(String shopName) {
        switch (shopName) {
            case "京东":
                return getPriceFromJD();
            case "淘宝":
                return getPriceFromTB();
            case "拼多多":
                return getPriceFromPDD();
        }
        return -1.0;
    }

    private static Future<Double> getPriceAsyncByShopNameAsync(String shopName) {
        switch (shopName) {
            case "京东":
                return CompletableFuture.supplyAsync(Price_5::getPriceFromJD);
            case "淘宝":
                return CompletableFuture.supplyAsync(Price_5::getPriceFromTB);
            case "拼多多":
                return CompletableFuture.supplyAsync(Price_5::getPriceFromPDD);
        }
        return null;
    }

    public static Double getPriceFromJD() {
        println(" calling 京东API");
        new Nap(3);
        println(" calling 京东API successful");
        return 300.0;
    }

    public static Double getPriceFromTB() {
        println(" calling 淘宝API");
        new Nap(3);
        println(" calling 淘宝API successful");
        return 200.0;
    }

    public static Double getPriceFromPDD() {
        println(" calling 拼多多API");
        new Nap(3);
        println(" calling 拼多多API successful");
        return 100.0;
    }

}
