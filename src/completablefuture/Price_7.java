package completablefuture;

import org.junit.Test;
import util.Nap;
import util.Timer;
import util._Print;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static util._Print.println;

/**
 * 优化后的性能差异就非常明显了
 *
 * 使用并行流还是CompletableFutrues?
 *     我们给出以下建议：
 *     如果你进行的是计算密集型的操作，并且没有I/O,推荐使用Stream接口，实现最为简单
 *     如果你进行的是操作设计I/O的操作，那么CompletableFuture灵活性更好。
 *
 *
 *     关于如何选择合适的线程数,在《Java并发编程实战》中作者给出了一些建议。如果线程池中线程过多，
 *     它们会竞争稀缺的处理器和内存资源，浪费大量的时间在上下文切换上。相反，如果线程的数目较少，
 *     处理器的一些核也就无法充分利用。该书的作者建议，线程池大小与处理器的利用率之比可以使用下面的公式进行估算
 *
 *     N(threads) = N(cpu)*U(cpu)*(1+W/C)
 *
 *     N(cpu)是处理器的核的数目，可以通过Runtime.getRuntime().availableProcessors()得到
 *     U(cpu)是期望CPU的利用率（该值应在0和1之间）
 *     W/C是等待时间与计算时间的比率
 */
public class Price_7 {
    private static final List<String> SHOPS;
    private static final ExecutorService EXECUTOR;

    static {
        SHOPS = IntStream.rangeClosed(2, 101)
                .mapToObj(i -> {
                    return "SHOP" + i;
                }).collect(Collectors.toList());
    }

    static {
        EXECUTOR = Executors.newFixedThreadPool(Math.min(SHOPS.size(), 2200), action -> {
            Thread t = new Thread(action);
            t.setDaemon(true);
            return t;
        });
    }

    @Test
    public void getPrice() {
//        getAllShopPriceByParallelStream();//一共耗时33956
        getAllShopPriceByCompletableFuture_v2(); //一共耗时387
//        System.out.println(Runtime.getRuntime().availableProcessors());12*1*200
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
                .map(Price_7::getPriceAsyncByShopNameAsync)
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
                return CompletableFuture.supplyAsync(Price_7::getPriceFromJD);
            case "淘宝":
                return CompletableFuture.supplyAsync(Price_7::getPriceFromTB,EXECUTOR);
            case "拼多多":
                return CompletableFuture.supplyAsync(Price_7::getPriceFromPDD,EXECUTOR);
            default:
                return CompletableFuture.supplyAsync(Price_7::getPriceFromShop,EXECUTOR);
        }
    }

    public static Double getPriceFromJD() {
        println(" calling 京东API");
        new Nap(300);
        println(" calling 京东API successful");
        return 300.0;
    }

    public static Double getPriceFromTB() {
        println(" calling 淘宝API");
        new Nap(300);
        println(" calling 淘宝API successful");
        return 200.0;
    }

    public static Double getPriceFromPDD() {
        println(" calling 拼多多API");
        new Nap(300);
        println(" calling 拼多多API successful");
        return 100.0;
    }

    public static Double getPriceFromShop() {
        println(" calling ShopAPI");
        new Nap(300);
        println(" calling ShopAPI successful");
        return 100.0;
    }


}
