package completablefuture;

import util.Nap;

import static util._Print.println;


public class DisCount {

    public enum Code {

        一折(10),
        两折(20),
        三折(30),
        四折(30),
        五折(30),
        六折(30);

        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    public static String getDisCountPrice(Quote quote){
        new Nap(10);
//        println("计算折后价格");
        int percentage = quote.code.percentage;
        double disCountPrice = quote.price * (100-percentage)/100;
        return String.format("商店：%s 的折后价格是：%f",quote.shopName,disCountPrice);
    }

    public static String getDisCountPriceRand(Quote quote){
        Nap.sleepRandom();
//        println("计算折后价格");
        int percentage = quote.code.percentage;
        double disCountPrice = quote.price * (100-percentage)/100;
        return String.format("商店：%s 的折后价格是：%f",quote.shopName,disCountPrice);
    }

}
