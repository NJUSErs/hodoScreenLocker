package njusoftware.hodoScreenLocker.TimeFactory;

import java.util.Calendar;

/**
 * Created by tinke on 2016/1/24.
 */
final class SolarTerm {
    private static double D = 0.2422;
    private static String[] SOLAR_TERMS = { "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑",
            "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至", "小寒", "大寒" };
    static double[] C = { 3.87, 18.73, 5.63, 20.646, 4.81, 20.1, 5.52, 21.01, 5.678, 21.37, 7.108, 22.83, 7.5, 23.13,
            7.646, 23.042, 8.318, 23.438, 7.438, 22.36, 7.18, 21.94, 5.4055, 20.12 };
    static int[][] EXCEPTION = { { -1, 0 }, { 26, -1 }, { -1, 0 }, { 84, 1 }, { -1, 0 }, { -1, 0 }, { -1, 0 }, { 8, 1 },
            { -1, 0 }, { -1, 0 }, { 16, 1 }, { -1, 0 }, { 2, 1 }, { -1, 0 }, { -1, 0 }, { -1, 0 }, { -1, 0 }, { 89, 1 },
            { 89, 1 }, { -1, 0 }, { -1, 0 }, { 21, -1 }, { 19, -1 }, { 82, 1 } };

    static String getSolarTerm(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int index = ((month + 22) % 12) * 2;
        int temp1 = calculateSolarTerm(year % 100, index, month);
        int temp2 = calculateSolarTerm(year % 100, index + 1, month);
        if (day < temp1)
            return SOLAR_TERMS[index == 0 ? 23 : index - 1];
        else if (temp1 <= day && day < temp2)
            return SOLAR_TERMS[index];
        else
            return SOLAR_TERMS[index + 1];
    }

    private static int calculateSolarTerm(int Y, int index, int month) {
        boolean isLeap = Y / 4 == 0 ? true : false;
        return (int) (Math.floor(Y * D + C[index]) - Math.floor((Y - (isLeap && month >= 3 ? 0 : 1)) / 4))
                + (Y == EXCEPTION[index][0] ? EXCEPTION[index][1] : 0);
    }
}
