package de.uhd.ifi.se.moviemanager.util;

import static java.util.Locale.US;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class DateUtils {

    public static final String FORMAT = "dd.MM.yyyy";

    // private constructor to prevent instantiation
    private DateUtils() {
        throw new UnsupportedOperationException();
    }

    public static Date normDateTimeToMidnight(Date d) {
        Date result;
        if (d == null) {
            result = null;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            result = calendar.getTime();
        }

        return result;
    }

    public static Date normDate(Date date) {
        if (date == null) {
            return null;
        }
        long normedMillis = normMillis(date.getTime());
        return new Date(normedMillis);
    }

    static long normMillis(long millis) {
        return 1000 * (millis / 1000);
    }

    public static Date now() {
        return new Date(normMillis(System.currentTimeMillis()));
    }

    public static Date nowAtMidnight() {
        return normDateTimeToMidnight(now());
    }

    public static long differenceInDays(Date date1, Date date2) {
        long millis1 = date1.getTime();
        long millis2 = date2.getTime();
        long diff = millis2 - millis1;
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static int differenceInYears(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        return Math.abs(calendar1.get(Calendar.YEAR) - calendar2
                .get(Calendar.YEAR));
    }

    public static String dateToText(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(FORMAT, US);
        formatter.setLenient(false);
        return dateToText(formatter, date);
    }

    public static String dateToText(SimpleDateFormat formatter, Date date) {
        String result;
        if (formatter == null || date == null) {
            result = "";
        } else {
            result = formatter.format(date);
        }
        return result;
    }

    public static Date textToDate(String dateAsString) {
        return textToDate(FORMAT, dateAsString);
    }

    public static Date textToDate(String format, String str) {
        return textToDate(new SimpleDateFormat(format, US), str);
    }

    public static Date textToDate(SimpleDateFormat formatter, String str) {
        Date result;

        if ("today".equals(str)) {
            result = now();
        } else if (str == null || formatter == null || str.isEmpty()) {
            result = null;
        } else {
            try {
                result = formatter.parse(str);
            } catch (ParseException e) {
                result = null;
            }
        }

        return result;
    }

    public static boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }

    public static long daysFromNow(Date d) {
        Date now = normDateTimeToMidnight(now());
        return differenceInDays(now, d);
    }
}
