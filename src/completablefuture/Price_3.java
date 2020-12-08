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
 * 对Price_2中描述的两个问题做调整
 */
public class Price_3 {

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
        CompletableFuture<Double> cf = new CompletableFuture<>();
        switch (shopName) {
            case "京东":
                new Thread(() -> {
                    try {
                        println(" calling 京东API");
                        new Nap(3);
                        int i = 1 / 0;//模拟异常
                        println(" calling 京东API successful");
                        cf.complete(300.0);
                    } catch (Exception ex) {
                        cf.completeExceptionally(ex);
                    }
                }).start();
                break;
            case "淘宝":
                new Thread(() -> {
                    try {
                        println(" calling 淘宝API");
                        new Nap(2);
                        println(" calling 淘宝API successful");
                        cf.complete(200.0);
                    } catch (Exception ex) {
                        cf.completeExceptionally(ex);
                    }
                }).start();
                break;
            case "拼多多":
                new Thread(() -> {
                    try {
                        println(" calling 拼多多API");
                        new Nap(5);
                        println(" calling 拼多多API successful");
                        cf.complete(100.0);
                    } catch (Exception ex) {
                        cf.completeExceptionally(ex);
                    }
                }).start();
                break;
        }
        return cf;
    }

}
