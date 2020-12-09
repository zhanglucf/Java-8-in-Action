package completablefuture;

import org.junit.Test;
import util.Nap;
import util.Timer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static util._Print.println;

/**
 *不等异步任务全部完成，有完成的就打印出来
 */
public class Price_9 {
    private static final Random RANDOM = new Random();
    private static final List<String> SHOPS;
    private static final ExecutorService EXECUTOR;

    static {
        SHOPS = IntStream.rangeClosed(2, 6)
                .mapToObj(i -> {
                    return "SHOP" + i;
                }).collect(Collectors.toList());
    }

    static {
        EXECUTOR = Executors.newFixedThreadPool(Math.min(SHOPS.size(), 6), action -> {
            Thread t = new Thread(action);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * 使用异步接口实现的优化版本使用thenApply
     * 比v1版本优雅很多，而且性能上也有提升
     * 一共耗时106
     *
     * @return
     */
    @Test
    public void getPrice() {
        println("*************");
        Timer timer = new Timer();
        CompletableFuture[] completableFutures = cf_v2()
                .map(f -> {
                    return f.thenAccept(c -> {
                        println(c + " ( " + "done in " + timer.duration() + " )");
                    });
                })
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(completableFutures).join();
        println(timer.duration());
        println("*************");
    }

    public static Stream<CompletableFuture<String>> cf_v2() {
        Timer timer = new Timer();
        //在调用各个商店价格接口时，开始异步任务 [1]
        return IntStream.rangeClosed(0, SHOPS.size())
                .mapToObj(i -> {
                    return CompletableFuture.supplyAsync(Price_9::getPriceFromShop, EXECUTOR)
                            .thenApply(Quote::parseQuote)
                            .thenApply(DisCount::getDisCountPriceRand);
                });
    }


    @Test
    public void cf_v3() {
        Timer timer = new Timer();
        //在调用各个商店价格接口时，开始异步任务 [1]
        CompletableFuture[] completableFutures = IntStream.rangeClosed(0, SHOPS.size())
                .mapToObj(i -> {
                    return CompletableFuture.supplyAsync(Price_9::getPriceFromShop, EXECUTOR)
                            .thenApply(Quote::parseQuote)
                            .thenApply(DisCount::getDisCountPriceRand);
                }).map(f -> {
                    return f.thenAccept(c -> {
                        println(c + " ( " + "done in " + timer.duration() + " )");
                    });
                }).toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(completableFutures).join();
    }


    public static String getPriceFromShop() {
        String shop = SHOPS.get(RANDOM.nextInt(SHOPS.size()));
//        println(String.format("calling %s API", shop));
        Nap.sleepRandom();
//        println(String.format(" calling %s API successful", shop));
        DisCount.Code code = DisCount.Code.values()[RANDOM.nextInt(DisCount.Code.values().length)];
        return String.format("%s:%.2f:%s", shop, 100.0, code);
    }


}
