package completablefuture;

import org.junit.Test;
import util.Nap;
import util.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static util.Nap.doSomeThing;
import static util._Print.println;

/**
 * 使用CompletableFuture中自带方法优化Price_3
 */
public class Price_4 {

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
        List<Double> prices = new ArrayList<>();
        for (Future<Double> f : fs) {
            try {
                //在这里，如果异步任务还没有结束，其实还是有可能阻塞主线程，
                prices.add(f.get(10, TimeUnit.SECONDS));     // [1]
            } catch (InterruptedException e) {
                println("异步方法被打断");
                e.printStackTrace();
            } catch (ExecutionException e) {
                println("异步方法发生异常");
                e.printStackTrace();
            } catch (TimeoutException e) {
                println("异步方法超时");
                e.printStackTrace();
            }
        }
        prices.sort(Double::compareTo);
        println("最便宜的价格:" + (prices.size() > 0 ? prices.get(0) : -1.0));
        println("共用时" + timer.duration());
    }

    private static Future<Double> getPriceAsyncByShopName(String shopName) {
        switch (shopName) {
            case "京东":
                return CompletableFuture.supplyAsync(() -> {
                    println(" calling 京东API");
                    new Nap(3);
//                    int i = 1 / 0;//模拟异常
                    println(" calling 京东API successful");
                    return 300.0;
                });
            case "淘宝":
                return CompletableFuture.supplyAsync(() -> {
                    println(" calling 淘宝API");
                    new Nap(3);
                    int i = 1 / 0;//模拟异常
                    println(" calling 淘宝API successful");
                    return 200.0;
                });
            case "拼多多":
                return CompletableFuture.supplyAsync(() -> {
                    println(" calling 拼多多API");
                    new Nap(3);
                    int i = 1 / 0;//模拟异常
                    println(" calling 拼多多API successful");
                    return 100.0;
                });
        }
        return null;
    }

}
