package njusoftware.hodoScreenLocker.TimeFactory;
import java.util.Calendar;
/**
 * Created by tinke on 2016/1/24.
 */
final public class TimeFactory {
    private Calendar cal;
    public static final int CLOCK = 1;
    public static final int DATE = 2;
    public static final int DAY_OF_WEEK = 3;
    public static final int TIME_OF_DAY = 4;
    public static final int LUNAR_CALENDAR = 5;
    public static final int SOLAR_TERM_AND_HOLIDAY = 6;

    public TimeFactory() {
        cal = Calendar.getInstance();
    }

    public String get(int field) {
        switch (field) {
            case CLOCK:
                return GregorianCalendar.getClock(cal);
            case DATE:
                return GregorianCalendar.getDate(cal);
            case DAY_OF_WEEK:
                return GregorianCalendar.getDayOfTheWeek(cal);
            case TIME_OF_DAY:
                return GregorianCalendar.getTimeOfTheDay(cal);
            case LUNAR_CALENDAR:
                return LunarCalendar.getLunarCalendar(cal);
            case SOLAR_TERM_AND_HOLIDAY:
                return SolarTerm.getSolarTerm(cal)+Holiday.getHoliday(cal);
            default:
                return null;
        }
    }
}
