package completablefuture;

import util._Print;

import static util._Print.println;

public class Quote {
    public String shopName;
    public Double price;
    public DisCount.Code code;

    private Quote(String shopName, Double price, DisCount.Code code) {
        this.shopName = shopName;
        this.price = price;
        this.code = code;
    }

    public static Quote parseQuote(String str) {
//        println("解析数据");
        String[] split = str.split(":");
        return new Quote(split[0], Double.valueOf(split[1]), DisCount.Code.valueOf(split[2]));
    }
}
