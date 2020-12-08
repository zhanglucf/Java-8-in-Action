package completablefuture;

import org.junit.Test;
import util.Nap;
import util.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static util.Nap.doSomeThing;
import static util._Print.println;

/**
 * 首次使用completableFuture,简单的例子
 * 下面例子有两个明显缺陷
 * [1]处有发生一直阻塞的可能，假如异步接口发生了异常，建议使用get的重载版本，设置一些超时参数
 * [2]处如果直接抛出异常，在主线程内是无法得知异步线程里究竟发生了什么，
 * 需要用到CompletableFuture 的completeExceptionally方法将异步线程中的异常抛出
 *
 * 这里其实可以和ExecutorService submit方法返回的Future做下对比
 * 往submit中传入的Callable任务中如果发生了异常，在主线程中使用get()是可以得知具体异常的，无需
 * 我们主动去传递异常，这点和CompletableFuture有着非常大的区别需要我们注意
 *
 * Price_3中对[1] [2]两处的提到的问题作出调整，具体内容查看Price_3
 * */
public class Price_2 {

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

        Double price = fs.stream()
                .map(f -> {
                    try {
                        //在这里，如果异步任务还没有结束，其实还是有可能阻塞主线程，
                        return f.get();     // [1]
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();}
//                    } catch (TimeoutException e) {
//                        e.printStackTrace();
//                    }
                    return null;
                })
                .collect(Collectors.minBy(Double::compare))
                .orElse(-1.0);
        println("最便宜的价格:" + price);
        println("共用时" + timer.duration());
    }

    private static Future<Double> getPriceAsyncByShopName(String shopName) {
        CompletableFuture<Double> cf = new CompletableFuture<>();
        switch (shopName) {
            case "京东":
                new Thread(() -> {
                    println(" calling 京东API");
                    new Nap(3);
//                    throw new RuntimeException();    [2]
                    println(" calling 京东API successful");
                    cf.complete(300.0);
                }).start();
                break;
            case "淘宝":
                new Thread(() -> {
                    println(" calling 淘宝API");
                    new Nap(2);
                    println(" calling 淘宝API successful");
                    cf.complete(200.0);
                }).start();
                break;
            case "拼多多":
                new Thread(() -> {
                    println(" calling 拼多多API");
                    new Nap(5);
                    println(" calling 拼多多API successful");

                    cf.complete(100.0);
                }).start();
                break;
        }
        return cf;
    }

}
