package localdatetime;

import org.junit.Test;

import java.time.*;
import java.time.temporal.*;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.TemporalAdjusters.*;

/**
 * 1 纳秒 = 1000皮秒
 * <p>
 * 1 微秒 = 1,000 纳秒
 * <p>
 * 1 毫秒 = 1,000,000 纳秒
 * <p>
 * 1 秒 = 1,000,000,000 纳秒
 */
public class Instant_1 {

    @Test
    public void f() {
        long l = System.currentTimeMillis();
        Instant instant = Instant.now();
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(instant.getNano());
        System.out.println(System.currentTimeMillis() - l);
        System.out.println(instant.toEpochMilli());

        Instant ofEpochSecond = Instant.ofEpochSecond(1);
        Instant ofEpochSecond2 = Instant.ofEpochSecond(l / 1000);
        System.out.println(ofEpochSecond);
        System.out.println(ofEpochSecond2);

    }

    @Test
    public void f2() throws InterruptedException {
        Instant before = Instant.now();
        TimeUnit.MILLISECONDS.sleep(3);
        Instant after = Instant.now();
        Duration between = Duration.between(before, after);
        Duration betweenLT = Duration.between(LocalTime.of(10, 30), LocalTime.of(16, 30));
        System.out.println(betweenLT);
        System.out.println(between);
        System.out.println(between.getNano());
        System.out.println(between.getSeconds());
    }

    @Test
    public void f3() throws InterruptedException {
        Period of = Period.of(1, 2, 4);
//        System.out.println(of);

        LocalDate localDate = LocalDate.of(2020, 12, 11);
//        System.out.println(localDate.plusYears(1).plusDays(1));
        //当时日期已经满足要求，直接返回该对象
        System.out.println(localDate.with(nextOrSame(DayOfWeek.THURSDAY)));
        System.out.println(localDate.with(previousOrSame(DayOfWeek.THURSDAY)));

        System.out.println(localDate.with(lastDayOfMonth()));

    }

    @Test
    public void dayOfWeekInMonthTest() {
        LocalDate now = LocalDate.of(2020, 12, 11);
        println("今天是", now);
        println("上个月的最后一个周一", now.with(dayOfWeekInMonth(0, DayOfWeek.MONDAY)));
        println("本月的第一个周一", now.with(dayOfWeekInMonth(1, DayOfWeek.MONDAY)));
        println("本月的第二个周一", now.with(dayOfWeekInMonth(2, DayOfWeek.MONDAY)));
        println("本月的第三个周一", now.with(dayOfWeekInMonth(3, DayOfWeek.MONDAY)));
        println("本月的第四个周一", now.with(dayOfWeekInMonth(4, DayOfWeek.MONDAY)));
        println("本月的第一天是", now.with(firstDayOfMonth()));
        println("下个月的第一天是", now.with(firstDayOfNextMonth()));
        println("明年第一天", now.with(firstDayOfNextYear()));
        println("今年第一天", now.with(firstDayOfNextYear()));
        println("本月的第一个周三", now.with(firstInMonth(DayOfWeek.WEDNESDAY)));
        println("本月的最后一天", now.with(lastDayOfMonth()));
        println("本年的最后一天", now.with(lastDayOfYear()));
        println("本月的最后一个周三", now.with(lastInMonth(DayOfWeek.WEDNESDAY)));
        println("下一个周三", now.with(nextOrSame(DayOfWeek.WEDNESDAY)));
        println("下一个周三", now.with(next(DayOfWeek.WEDNESDAY)));
        println("下一个工作日", now.with(nextWorkingDay));

    }


    @Test
    public void f4() throws InterruptedException {
        ZoneId ctt = ZoneId.of(ZoneId.SHORT_IDS.get("CTT"));
        ZoneId ctt2 = ZoneId.of("Asia/Shanghai");
        ZoneId zoneId = TimeZone.getDefault().toZoneId();
        LocalDate localDate = LocalDate.now();
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);
        System.out.println(zonedDateTime);
        ZoneId of = ZoneOffset.of("+08:00");
        System.out.println(of.getId());
    }


    TemporalAdjuster nextWorkingDay = t -> {
        System.out.println(t instanceof LocalDate);
        int dayOfWeekInt = t.get(ChronoField.DAY_OF_WEEK);
        DayOfWeek dayOfWeek = DayOfWeek.of(dayOfWeekInt);
        if (DayOfWeek.FRIDAY == dayOfWeek) {
            return t.plus(3, ChronoUnit.DAYS);
        } else if (DayOfWeek.SATURDAY == dayOfWeek) {
            return t.plus(2, ChronoUnit.DAYS);
        } else {
            return t.plus(1, ChronoUnit.DAYS);
        }
    };


    public static void println(String str, Object obj) {
        System.out.println(String.format("%s %s", str, obj));
    }
}
