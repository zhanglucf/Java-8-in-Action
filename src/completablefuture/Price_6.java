package completablefuture;

import org.junit.Test;
import util.Nap;
import util.Timer;
import util._Print;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static util._Print.println;

/**
 * 重点比较getAllShopPriceByCompletableFuture_v2
 * 与getAllShopPriceByParallelStream的性能差异
 * 从结果上看好像没有明显差异，
 * Price_7对CompletableFuture版本的实现做进一步优化
 * 优化后的性能差异就非常明显了
 */
public class Price_6 {
//    private static final List<String> SHOPS = Arrays.asList("京东", "淘宝", "拼多多");
    private static final List<String> SHOPS;

static {
    SHOPS = IntStream.rangeClosed(2, 101)
            .mapToObj(i -> {
                return "SHOP" + i;
            }).collect(Collectors.toList());
}

    @Test
    public void getPrice() {
//        getAllShopPriceByParallelStream();//一共耗时27011
        getAllShopPriceByCompletableFuture_v2(); //一共耗时30024
//        System.out.println(Runtime.getRuntime().availableProcessors());
    }


    /**
     * 使用异步接口实现
     * 使用了两条不同的Stream流水线，这样的好处是：
     * 第一条流水线先分配任务，异步线程可以先开始工作
     * 第二条流水线阻塞主线程，等待异步接口给出结果
     * 性能的瓶颈在于所有接口中最慢的那个
     *
     * @return
     */
    private static List<String> getAllShopPriceByCompletableFuture_v2() {
        println("parallelStream");
        Timer timer = new Timer();
        List<Future<Double>> futureList = SHOPS.stream()
                .map(Price_6::getPriceAsyncByShopNameAsync)
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
        println("查询所有商点 关于 <<JAVA并发编程实战>>的价格 一共耗时" + timer.duration());
        println("各个商店<<JAVA并发编程实战>>的价格分别为：");
        prices.forEach(_Print::println);
        return prices;
    }

    /**
     * 并行流实现方式
     *
     * @return
     */
    private static List<String> getAllShopPriceByParallelStream() {
        println("parallelStream");
        Timer timer = new Timer();
        List<String> prices = SHOPS.parallelStream()
                .map(x -> {
                    return x + "的价格是:" + getPriceByShopName(x);
                }).collect(Collectors.toList());
        println("查询所有商点 关于 <<JAVA并发编程实战>>的价格 一共耗时" + timer.duration());
        println("各个商店<<JAVA并发编程实战>>的价格分别为：");
        prices.forEach(_Print::println);
        return prices;
    }

    private static Double getPriceByShopName(String shopName) {
        switch (shopName) {
            case "京东":
                return getPriceFromJD();
            case "淘宝":
                return getPriceFromTB();
            case "拼多多":
                return getPriceFromPDD();
            default:
                return getPriceFromShop();
        }
    }

    private static Future<Double> getPriceAsyncByShopNameAsync(String shopName) {
        switch (shopName) {
            case "京东":
                return CompletableFuture.supplyAsync(Price_6::getPriceFromJD);
            case "淘宝":
                return CompletableFuture.supplyAsync(Price_6::getPriceFromTB);
            case "拼多多":
                return CompletableFuture.supplyAsync(Price_6::getPriceFromPDD);
            default:
                return CompletableFuture.supplyAsync(Price_6::getPriceFromJD);
        }
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

    public static Double getPriceFromShop() {
        println(" calling ShopAPI");
        new Nap(3);
        println(" calling ShopAPI successful");
        return 100.0;
    }


}
