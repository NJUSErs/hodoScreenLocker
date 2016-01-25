package njusoftware.hodoScreenLocker.TimeFactory;

import java.util.Calendar;

/**
 * Created by tinke on 2016/1/24.
 */
final class Holiday {
    private static String SPACE = " ";
    private static String NOTHING = "";
    private static int[][] DATE_OF_GREGORIAN_CALENDAR_HOLIDAY1 = { { 1 }, { 14 }, { 8, 12, 15 }, { 1, 7, 22 },
            { 1, 4, 8 }, { 1 }, { 1, 7 }, { 1 }, { 3, 10, 18, 21 }, { 1, 31 }, { 10 }, { 13, 24, 25 } };

    private static String[][] NAME_OF_GREGORIAN_CALENDAR_HOLIDAY1 = { { "元旦" }, { "情人节" }, { "国际劳动妇女节", "国际消费者权益日" },
            { "愚人节", "世界卫生日", "世界地球日" }, { "国际劳动节", "中国青年节", "世界红十字日" }, { "国际儿童节" }, { "中国建党节", "中国人民抗战纪念日" },
            { "抗战胜利纪念日" }, { "中国教师节", "中国国耻日", "国际和平日" }, { "国庆节", "万圣节" }, { "世界青年日" }, { "南京大屠杀纪念日", "平安夜", "圣诞节" } };

    private static int[][][] DATE_OF_GREGORIAN_CALENDAR_HOLIDAY2 = { {}, {}, {}, {}, { { 2, 1 } }, { { 3, 5 } }, {}, {},
            {}, {}, { { 4, 5 } }, {} };

    private static String[][] NAME_OF_GREGORIAN_CALENDAR_HOLIDAY2 = { {}, {}, {}, {}, { "母亲节" }, { "父亲节" }, {}, {}, {},
            {}, { "感恩节" }, {} };

    private static int[][] DATE_OF_LUNAR_CALENDAR_HOLIDAY = { { 1, 15 }, {}, {}, {}, { 5 }, {}, { 7, 15 }, { 15 },
            { 9 }, {}, {}, { 8, 23 } };

    private static String[][] NAME_OF_LUNAR_CALENDAR_HOLIDAY = { { "春节", "元宵节" }, {}, {}, {}, { "端午节" }, {},
            { "七夕", "中元节" }, { "中秋节" }, { "重阳节" }, {}, {}, { "腊八节", "小年" } };

    static String getHoliday(Calendar cal) {
        return getFirstHoliday(cal) + getSecondHoliday(cal) + getThirdHoliday(cal) + getOtherHoliday(cal);
    }

    private static String getFirstHoliday(Calendar cal) {
        int month = cal.get(Calendar.MONTH);
        for (int i = 0; i < (DATE_OF_GREGORIAN_CALENDAR_HOLIDAY1[month].length); i++) {
            if (DATE_OF_GREGORIAN_CALENDAR_HOLIDAY1[month][i] == cal.get(Calendar.DAY_OF_MONTH))
                return SPACE + NAME_OF_GREGORIAN_CALENDAR_HOLIDAY1[month][i];
        }
        return NOTHING;
    }

    private static String getSecondHoliday(Calendar cal) {
        int[] temp = LunarCalendar.calculate(cal);
        int month = temp[1] - 1;
        int day = temp[2];
        if (temp[3] == 1)
            return NOTHING;
        for (int i = 0; i < (DATE_OF_LUNAR_CALENDAR_HOLIDAY[month].length); i++) {
            if (DATE_OF_LUNAR_CALENDAR_HOLIDAY[month][i] == day)
                return SPACE + NAME_OF_LUNAR_CALENDAR_HOLIDAY[month][i];
        }
        return NOTHING;
    }

    private static String getThirdHoliday(Calendar cal) {
        int month = cal.get(Calendar.MONTH);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int dayOfWeekInMonth = cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        for (int i = 0; i < DATE_OF_GREGORIAN_CALENDAR_HOLIDAY2[month].length; i++) {
            if (DATE_OF_GREGORIAN_CALENDAR_HOLIDAY2[month][i][1] == dayOfWeek) {
                if (DATE_OF_GREGORIAN_CALENDAR_HOLIDAY2[month][i][0] == dayOfWeekInMonth)
                    return SPACE + NAME_OF_GREGORIAN_CALENDAR_HOLIDAY2[month][i];
            }
        }
        return NOTHING;
    }

    private static String getOtherHoliday(Calendar cal) {
        cal.add(Calendar.DAY_OF_MONTH, 1);
        if (getSecondHoliday(cal).equals(" 春节"))
            return " 除夕";
        if (SolarTerm.getSolarTerm(cal).equals("清明"))
            return " 寒食";
        return NOTHING;
        // 复活节的计算太复杂了，建议直接列出来吧，我是不想弄了……
    }
}
