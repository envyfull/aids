/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.utils;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.dragonmc.core.common.language.Language;

public class DateUtils {
    public static String getTime(Language language, long expire) {
        String string = DateUtils.formatDifference(language, (expire - System.currentTimeMillis()) / 1000L);
        if (string == null || string.isEmpty()) {
            string = "0 " + language.t("second", new String[0]);
        }
        return string.trim();
    }

    public static String formatTime(long time, DecimalFormat decimalFormat) {
        double seconds = (double)(time - System.currentTimeMillis()) / 1000.0;
        return decimalFormat.format(seconds);
    }

    public static long getMidNight() {
        GregorianCalendar date = new GregorianCalendar();
        date.set(11, 0);
        date.set(12, 0);
        date.set(13, 0);
        date.set(14, 0);
        ((Calendar)date).add(5, 1);
        return date.getTimeInMillis();
    }

    public static String formatDifference(Language language, long time) {
        String diff;
        if (time == 0L) {
            return "";
        }
        long day = TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - day * 24L;
        long minutes = TimeUnit.SECONDS.toMinutes(time) - TimeUnit.SECONDS.toHours(time) * 60L;
        long seconds = TimeUnit.SECONDS.toSeconds(time) - TimeUnit.SECONDS.toMinutes(time) * 60L;
        StringBuilder sb = new StringBuilder();
        if (day > 0L) {
            sb.append(day).append(" ").append(language.t("day", new String[0]) + (day == 1L ? "" : "s")).append(" ");
        }
        if (hours > 0L) {
            sb.append(hours).append(" ").append(language.t("hour", new String[0]) + (hours == 1L ? "" : "s")).append(" ");
        }
        if (minutes > 0L) {
            sb.append(minutes).append(" ").append(language.t("minute", new String[0]) + (minutes == 1L ? "" : "s")).append(" ");
        }
        if (seconds > 0L) {
            sb.append(seconds).append(" ").append(language.t("second", new String[0]) + (seconds == 1L ? "" : "s"));
        }
        return (diff = sb.toString()).isEmpty() ? "0 " + language.t("second", new String[0]) : diff;
    }

    public static Long getTime(String string) {
        try {
            return DateUtils.parseDateDiff(string, true) + 500L;
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static String getDifferenceFormat(Language language, long timestamp) {
        return DateUtils.formatDifference(language, timestamp - System.currentTimeMillis() / 1000L);
    }

    public static long parseDateDiff(String time, boolean future) throws Exception {
        Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);
        Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) continue;
            for (int i = 0; i < m.groupCount(); ++i) {
                if (m.group(i) == null || m.group(i).isEmpty()) continue;
                found = true;
                break;
            }
            if (!found) continue;
            if (m.group(1) != null && !m.group(1).isEmpty()) {
                years = Integer.parseInt(m.group(1));
            }
            if (m.group(2) != null && !m.group(2).isEmpty()) {
                months = Integer.parseInt(m.group(2));
            }
            if (m.group(3) != null && !m.group(3).isEmpty()) {
                weeks = Integer.parseInt(m.group(3));
            }
            if (m.group(4) != null && !m.group(4).isEmpty()) {
                days = Integer.parseInt(m.group(4));
            }
            if (m.group(5) != null && !m.group(5).isEmpty()) {
                hours = Integer.parseInt(m.group(5));
            }
            if (m.group(6) != null && !m.group(6).isEmpty()) {
                minutes = Integer.parseInt(m.group(6));
            }
            if (m.group(7) == null || m.group(7).isEmpty()) break;
            seconds = Integer.parseInt(m.group(7));
            break;
        }
        if (!found) {
            throw new Exception("Illegal Date");
        }
        if (years > 100) {
            throw new Exception("Illegal Date");
        }
        GregorianCalendar c = new GregorianCalendar();
        if (years > 0) {
            ((Calendar)c).add(1, years * (future ? 1 : -1));
        }
        if (months > 0) {
            ((Calendar)c).add(2, months * (future ? 1 : -1));
        }
        if (weeks > 0) {
            ((Calendar)c).add(3, weeks * (future ? 1 : -1));
        }
        if (days > 0) {
            ((Calendar)c).add(5, days * (future ? 1 : -1));
        }
        if (hours > 0) {
            ((Calendar)c).add(11, hours * (future ? 1 : -1));
        }
        if (minutes > 0) {
            ((Calendar)c).add(12, minutes * (future ? 1 : -1));
        }
        if (seconds > 0) {
            ((Calendar)c).add(13, seconds * (future ? 1 : -1));
        }
        return c.getTimeInMillis();
    }

    public static boolean isForever(long time) {
        return time - System.currentTimeMillis() >= 62208000000L;
    }
}

