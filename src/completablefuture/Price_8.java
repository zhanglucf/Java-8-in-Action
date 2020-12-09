package completablefuture;

import org.junit.Test;
import util.Nap;
import util.Timer;
import util._Print;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static util._Print.println;

/**
 *
 */
public class Price_8 {
    private static final Random RANDOM = new Random();
    private static final List<String> SHOPS;
    private static final ExecutorService EXECUTOR;

    static {
        SHOPS = IntStream.rangeClosed(2, 501)
                .mapToObj(i -> {
                    return "SHOP" + i;
                }).collect(Collectors.toList());
    }

    static {
        EXECUTOR = Executors.newFixedThreadPool(Math.min(SHOPS.size(), 500), action -> {
            Thread t = new Thread(action);
            t.setDaemon(true);
            return t;
        });
    }

    private static List<String> getAllShopPriceByCompletableFuture_v3() {
        println("parallelStream");
        Timer timer = new Timer();
        List<Future<String>> futureList = IntStream.rangeClosed(0, SHOPS.size())
                .mapToObj(i -> getPriceAsyncByShopNameThenApplyAsync())
                .collect(Collectors.toList());

        List<String> prices = futureList.stream()
                .map(f -> {
                    String price = null;
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
//        prices.forEach(_Print::println);
        return prices;
    }


    /**
     * 同步顺序执行（查询500个接口，每个接口耗时10ms,计算耗时10ms）
     *一共耗时10754
     * @return
     */
    @Test
    public void getAllShopPrice() {
        println("parallelStream");
        Timer timer = new Timer();
        SHOPS.stream()
                .map(Price_8::getPrice)
                .map(Quote::parseQuote)
                .map(DisCount::getDisCountPrice)
                .collect(Collectors.toList());
        println("查询所有商点 关于 <<JAVA并发编程实战>>的价格 一共耗时" + timer.duration());
    }

    /**
     * 并行流（查询500个接口，每个接口耗时10ms,计算耗时10ms）
     *一共耗时1021
     * 说明：
     *  三个map串联 处理同一条数据时，三个map被同一个线程执行到
     * @return
     */
    @Test
    public void getAllShopPriceParallel() {
        println("parallelStream");
        Timer timer = new Timer();
        SHOPS.parallelStream()
                .map(Price_8::getPrice)
                .map(Quote::parseQuote)
                .map(DisCount::getDisCountPrice)
                .collect(Collectors.toList());
        println("查询所有商点 关于 <<JAVA并发编程实战>>的价格 一共耗时" + timer.duration());
    }

    /**
     * 使用异步接口实现
     * 连续开启两次异步任务，实现起来也比较繁琐
     *一共耗时108
     * @return
     */
    @Test
    public void cf_v1() {
        Timer timer = new Timer();
        //在调用各个商店价格接口时，开始异步任务 [1]
        List<CompletableFuture<String>> futureList = IntStream.rangeClosed(0, SHOPS.size())
                .mapToObj( i -> {
                    return CompletableFuture.supplyAsync(Price_8::getPriceFromShop, EXECUTOR);
                })
                .collect(Collectors.toList());

        //获取异步任务结果，解析数据，计算折后价格再次开启异步任务 [2]
        List<CompletableFuture<String>> fs= futureList.stream()
                .map(f -> {
                    CompletableFuture<String> ff =   CompletableFuture.supplyAsync(() -> {
                        Quote quote = null;
                        quote = Quote.parseQuote(f.join());
                        return DisCount.getDisCountPrice(quote);
                    },EXECUTOR);
                    return ff;
                }).collect(Collectors.toList());

        //获取异步任务结果 [3]
        fs.stream()
                .map(CompletableFuture::join)
                .peek(System.out::println)
                .collect(Collectors.toList());
        println("查询所有商点 关于 <<JAVA并发编程实战>>的价格 一共耗时" + timer.duration());
    }


    /**
     * 使用异步接口实现的优化版本使用thenApply
     * 比v1版本优雅很多，而且性能上也有提升
     *一共耗时106
     * @return
     */
    @Test
    public void cf_v2() {
        Timer timer = new Timer();
        //在调用各个商店价格接口时，开始异步任务 [1]
        List<CompletableFuture<String>> fs = IntStream.rangeClosed(0, SHOPS.size())
                .mapToObj( i -> {
                    return CompletableFuture.supplyAsync(Price_8::getPriceFromShop, EXECUTOR)
                            .thenApply(Quote::parseQuote)
                            .thenApply(DisCount::getDisCountPrice);
                }).collect(Collectors.toList());

        fs.stream()
                .map(CompletableFuture::join)
                .peek(System.out::println)
                .collect(Collectors.toList());
        println("查询所有商点 关于 <<JAVA并发编程实战>>的价格 一共耗时" + timer.duration());
    }



    private static String getPrice(String shopName) {
        return getPriceFromShop();

    }

    private static Future<String> getPriceAsyncByShopNameThenApplyAsync() {
        return CompletableFuture.supplyAsync(Price_8::getPriceFromShop, EXECUTOR).thenApply(x -> {
            return DisCount.getDisCountPrice(Quote.parseQuote(x));
        });
    }

    private static Future<String> getPriceAsyncByShopNameAsync() {
        return CompletableFuture.supplyAsync(Price_8::getPriceFromShop, EXECUTOR);
    }

    public static String getPriceFromJD() {
        println(" calling 京东API");
        new Nap(300);
        println(" calling 京东API successful");
        DisCount.Code code = DisCount.Code.values()[RANDOM.nextInt(DisCount.Code.values().length)];
        return String.format("%s:%f:5s", "京东", 300.0, code);
    }

    public static String getPriceFromTB() {
        println(" calling 淘宝API");
        new Nap(10);
        println(" calling 淘宝API successful");
        DisCount.Code code = DisCount.Code.values()[RANDOM.nextInt(DisCount.Code.values().length)];
        return String.format("%s:%.2f:5s", "淘宝", 200.0, code);
    }

    public static String getPriceFromPDD() {
        println(" calling 拼多多API");
        new Nap(10);
        println(" calling 拼多多API successful");
        DisCount.Code code = DisCount.Code.values()[RANDOM.nextInt(DisCount.Code.values().length)];
        return String.format("%s:%.2f:5s", "拼多多", 100.0, code);
    }

    public static String getPriceFromShop() {
        String shop = SHOPS.get(RANDOM.nextInt(SHOPS.size()));
        println(String.format("calling %s API", shop));
        new Nap(10);
        println(String.format(" calling %s API successful", shop));
        DisCount.Code code = DisCount.Code.values()[RANDOM.nextInt(DisCount.Code.values().length)];
        return String.format("%s:%.2f:%s", shop, 100.0, code);
    }


}
