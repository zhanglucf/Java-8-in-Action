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
        getAllShopPriceByParallelStream();

    }


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
