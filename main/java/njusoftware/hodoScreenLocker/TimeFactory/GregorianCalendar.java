package njusoftware.hodoScreenLocker.TimeFactory;

import java.util.Calendar;

/**
 * Created by tinke on 2016/1/24.
 */
public final class GregorianCalendar {
    private static String[] str = { "日", "一", "二", "三", "四", "五", "六" };

    static String getClock(Calendar c) {
        return c.get(Calendar.HOUR_OF_DAY) + ":" + (String.format("%02d", c.get(Calendar.MINUTE)));
    }

    static String getDate(Calendar c) {
        return ((c.get(Calendar.MONTH) + 1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日");
    }

    static String getDayOfTheWeek(Calendar c) {
        return "星期" + str[c.get(Calendar.DAY_OF_WEEK) - 1];
    }

    static String getTimeOfTheDay(Calendar c) {
        int tempHour = c.get(Calendar.HOUR_OF_DAY);
        if (tempHour == 23 || tempHour < 3)
            return "深夜";
        else if (tempHour >= 3 && tempHour < 6)
            return "凌晨";
        else if (tempHour >= 6 && tempHour < 9)
            return "早晨";
        else if (tempHour >= 9 && tempHour < 12)
            return "上午";
        else if (tempHour == 12)
            return "中午";
        else if (tempHour >= 13 && tempHour < 17)
            return "下午";
        else if (tempHour >= 17 && tempHour < 19)
            return "黄昏";
        else if (tempHour >= 19 && tempHour < 23)
            return "晚上";
        return null;
    }
}
