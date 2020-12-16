package localdate;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;

public class LocalDate_1 {

    @Test
    public void f(){
        LocalDate ld = LocalDate.of(2012, 12, 16);

        println("getYear()",ld.getYear());
        println("getMonth()",ld.getMonth());
        println("getDayOfMonth()",ld.getDayOfMonth());
        println("getDayOfWeek()",ld.getDayOfWeek());
        println("getDayOfYear()",ld.getDayOfYear());
        println("getEra()",ld.getEra());
        println("getChronology()",ld.getChronology());

        LocalDate now = LocalDate.now();
        println("LocalDate.now()",now);
        println("YEAR",now.get(ChronoField.YEAR));
        println("MONTH_OF_YEAR",now.get(ChronoField.MONTH_OF_YEAR));
        println("DAY_OF_MONTH",now.get(ChronoField.DAY_OF_MONTH));
//        LocalDate parse = LocalDate.parse("2012-12-11");
        LocalDate parse = LocalDate.parse("2012-12-11");
        System.out.println(parse);
    }

    @Test
    public void f2(){
        LocalTime localTime = LocalTime.of(10, 30, 30);
        println("localTime.getMinute()",localTime.getMinute());
        println("localTime.getNano()",localTime.getNano());
        println("localTime",localTime);
        LocalTime parse = LocalTime.parse("23:30:30");
        println("parse",parse);
    }

    @Test
    public void f3(){
        LocalDateTime ldf = LocalDateTime.of(2012, Month.MAY, 21, 12, 30, 17);
        println("of",ldf);

        LocalDate date = LocalDate.of(2012, 12, 16);
        LocalTime time = LocalTime.of(10, 30, 30);

        LocalDateTime dt = LocalDateTime.of(date, time);
        System.out.println(dt);

        LocalDateTime dt2 = date.atTime(12, 12, 15);
        System.out.println(dt2);

        LocalDateTime dt3 = time.atDate(date);
        System.out.println(dt3);

        LocalDate localDate = ldf.toLocalDate();
        LocalTime localTime = ldf.toLocalTime();
        System.out.println(localDate);
        System.out.println(localTime);

    }

    @Test
    public void ff(){
        byte b = 1;
        short sh = 1;
        int in = 1;
        long lo = 1L;
        char ch = 'a';
        float fl = 1.0f;
        double dou = 2.0;
        boolean boo = true;
        println("byte",b);
        println("short",sh);
        println("int",in);
        println("long",lo);
        println("char",ch);
        println("float",fl);
        println("double",dou);
        println("boolean",boo);
        println("Object",new Object());
    }

    public static void println(String str,Object val){
        System.out.format("1 %s : %s%n", str, val);
    }
}
