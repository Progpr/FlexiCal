package calendar.model.modelimplementations;

import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import javax.management.openmbean.KeyAlreadyExistsException;

/**
 * Class to implement the calendar manager interface.
 *
 */

public class CalendarManagerImpl implements CalendarManager {
  private final Map<String, Calendar> calendarManagerStore;
  private String currentCalendarName;

  /**
   * Constructor to construct the calendarManagerImpl class.
   */

  public CalendarManagerImpl() {
    this.calendarManagerStore = new HashMap<>();
    this.currentCalendarName = null;
  }

  /**
   * creates deep copy.
   *
   * @param nameOfCalendar name of calendar
   * @param timeZone timezone
   */

  public CalendarManagerImpl(String nameOfCalendar, String timeZone) {
    this.calendarManagerStore = new HashMap<>();
    Calendar newCal = this.createCalendar(nameOfCalendar, timeZone);
    this.saveCalendar(newCal);
    this.currentCalendarName = nameOfCalendar;
  }

  @Override
  public void setCurrentCalendarNameAs(String calendarName) {
    this.currentCalendarName = calendarName;
  }

  @Override
  public String getCurrentCalendarName() {
    return this.currentCalendarName;
  }

  private void validateCalendar(Calendar calendar) throws KeyAlreadyExistsException {
    for (Map.Entry<String, Calendar> entry : getCalendarManagerStore().entrySet()) {
      if (entry.getKey().equals(calendar.getCalendarName())) {
        throw new KeyAlreadyExistsException("Calendar already exists!");
      }
    }
  }

  @Override
  public Map<String, Calendar> getCalendarManagerStore() {
    return this.calendarManagerStore;
  }

  @Override
  public Calendar createCalendar(String calendarName, String timeZone) {
    ZoneId zoneId = null;

    try {
      zoneId = ZoneId.of(timeZone);
      return new CalendarImpl(calendarName, zoneId);
    } catch (Exception e) {
      System.out.println("Timezone not valid: " + e.getMessage());
      return null;
    }
  }

  @Override
  public Calendar getCalendar(String calendarName) {
    if (calendarManagerStore.containsKey(calendarName)) {
      return calendarManagerStore.get(calendarName);
    }
    return null;
  }

  @Override
  public void saveCalendar(Calendar calendar) {
    try {
      validateCalendar(calendar);
    } catch (KeyAlreadyExistsException e) {
      System.out.println(e.getMessage());
    }

    getCalendarManagerStore().put(calendar.getCalendarName(), calendar);

  }
}
