package com.wyl.treasury.basic;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class InstantTest {
    public static void main(String[] args) {
        System.out.println(new Date());
        Instant instant = Instant.now();
        System.out.println(instant);
        System.out.println(instant.truncatedTo(ChronoUnit.DAYS));
        System.out.println(instant.truncatedTo(ChronoUnit.HOURS));
        instant = Instant.ofEpochMilli(new Date().getTime());
        System.out.println(instant);
        System.out.println(instant.plus(1, ChronoUnit.DAYS));
        System.out.println(instant.plus(Duration.ofHours(5).plusMinutes(4)));
        ChronoUnit.MINUTES.between(instant, instant);
        System.out.println(instant.until(instant.minus(10, ChronoUnit.MINUTES), ChronoUnit.SECONDS));
        System.out.println("========================");
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate);
        LocalTime localTime = LocalTime.now();
        System.out.println(localTime);
        System.out.println(LocalTime.ofSecondOfDay(3600 * 6));

        System.out.println("LocalDateTime.now()===" + LocalDateTime.now());
        System.out.println("Instant.now()=========" + Instant.now());
        System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));

    }
}
