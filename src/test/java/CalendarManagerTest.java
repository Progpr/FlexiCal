import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import calendar.model.modelimplementations.CalendarImpl;
import calendar.model.modelimplementations.CalendarManagerImpl;
import calendar.model.modelinterfaces.Calendar;
import java.time.ZoneId;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive unit test class for CalendarManagerImpl.
 * Tests every line and branch of code for 100% coverage.
 */
public class CalendarManagerTest {

  private CalendarManagerImpl calendarManager;
  private Calendar testCalendar1;
  private Calendar testCalendar2;

  /**
   * set up for calendarManager tests.
   */
  @Before
  public void setUp() {
    calendarManager = new CalendarManagerImpl();
    testCalendar1 = new CalendarImpl("TestCalendar1", ZoneId.of("America/New_York"));
    testCalendar2 = new CalendarImpl("TestCalendar2", ZoneId.of("Europe/London"));
  }

  @Test
  public void testConstructor() {
    CalendarManagerImpl manager = new CalendarManagerImpl();

    assertNotNull(manager.getCalendarManagerStore());
    assertEquals(0, manager.getCalendarManagerStore().size());
    assertNull(manager.getCurrentCalendarName());
  }

  @Test
  public void testSetCurrentCalendarNameAs() {
    assertNull(calendarManager.getCurrentCalendarName());

    String calendarName = "MyCalendar";
    calendarManager.setCurrentCalendarNameAs(calendarName);

    assertEquals(calendarName, calendarManager.getCurrentCalendarName());
  }

  @Test
  public void testSetCurrentCalendarNameAsNull() {
    calendarManager.setCurrentCalendarNameAs("SomeName");
    calendarManager.setCurrentCalendarNameAs(null);

    assertNull(calendarManager.getCurrentCalendarName());
  }

  @Test
  public void testGetCurrentCalendarName() {
    assertNull(calendarManager.getCurrentCalendarName());

    String name = "TestName";
    calendarManager.setCurrentCalendarNameAs(name);
    assertEquals(name, calendarManager.getCurrentCalendarName());
  }

  @Test
  public void testGetCalendarManagerStore() {
    Map<String, Calendar> store = calendarManager.getCalendarManagerStore();

    assertNotNull(store);
    assertEquals(0, store.size());

    store.put("test", testCalendar1);
    assertEquals(1, calendarManager.getCalendarManagerStore().size());
  }

  @Test
  public void testCreateCalendarValidTimezone() {
    String calendarName = "ValidCalendar";
    String timeZone = "America/Chicago";

    Calendar calendar = calendarManager.createCalendar(calendarName, timeZone);

    assertNotNull(calendar);
    assertEquals(calendarName, calendar.getCalendarName());
    assertEquals(ZoneId.of(timeZone), calendar.getCalendarTimeZone());
  }

  @Test
  public void testCreateCalendarVariousValidTimezones() {
    String[] validTimeZones = {
        "America/New_York",
        "America/Los_Angeles",
        "America/Chicago",
        "Europe/London",
        "Europe/Paris",
        "Asia/Tokyo",
        "Asia/Shanghai",
        "Australia/Sydney",
        "Pacific/Auckland"
    };

    for (String tz : validTimeZones) {
      Calendar cal = calendarManager.createCalendar("Cal_" + tz.replace("/", "_"), tz);
      assertNotNull(cal);
      assertEquals(ZoneId.of(tz), cal.getCalendarTimeZone());
    }
  }

  @Test
  public void testCreateCalendarInvalidTimezone() {
    String calendarName = "InvalidCalendar";
    String invalidTimeZone = "Invalid/Timezone";

    Calendar calendar = calendarManager.createCalendar(calendarName, invalidTimeZone);

    assertNull(calendar);
  }

  @Test
  public void testCreateCalendarNullTimezone() {
    String calendarName = "NullTimezoneCalendar";

    Calendar calendar = calendarManager.createCalendar(calendarName, null);

    assertNull(calendar);
  }

  @Test
  public void testCreateCalendarEmptyCalendarName() {
    String emptyName = "";
    String timeZone = "America/New_York";

    Calendar calendar = calendarManager.createCalendar(emptyName, timeZone);

    assertNull(calendar);
  }

  @Test
  public void testGetCalendarExists() {
    String calendarName = "ExistingCalendar";
    calendarManager.getCalendarManagerStore().put(calendarName, testCalendar1);

    Calendar retrieved = calendarManager.getCalendar(calendarName);

    assertNotNull(retrieved);
    assertSame(testCalendar1, retrieved);
  }

  @Test
  public void testGetCalendarNotExists() {
    String nonExistentName = "NonExistentCalendar";

    Calendar retrieved = calendarManager.getCalendar(nonExistentName);

    assertNull(retrieved);
  }

  @Test
  public void testGetCalendarEmptyStore() {
    assertEquals(0, calendarManager.getCalendarManagerStore().size());

    Calendar retrieved = calendarManager.getCalendar("AnyName");

    assertNull(retrieved);
  }

  @Test
  public void testSaveCalendarSuccess() {
    assertEquals(0, calendarManager.getCalendarManagerStore().size());

    calendarManager.saveCalendar(testCalendar1);

    assertEquals(1, calendarManager.getCalendarManagerStore().size());
    assertTrue(
        calendarManager.getCalendarManagerStore().containsKey(testCalendar1.getCalendarName()));
    assertSame(testCalendar1,
        calendarManager.getCalendarManagerStore().get(testCalendar1.getCalendarName()));
  }

  @Test
  public void testSaveCalendarMultiple() {
    calendarManager.saveCalendar(testCalendar1);
    calendarManager.saveCalendar(testCalendar2);

    assertEquals(2, calendarManager.getCalendarManagerStore().size());
    assertTrue(
        calendarManager.getCalendarManagerStore().containsKey(testCalendar1.getCalendarName()));
    assertTrue(
        calendarManager.getCalendarManagerStore().containsKey(testCalendar2.getCalendarName()));
  }

  @Test
  public void testSaveCalendarDuplicate() {
    calendarManager.saveCalendar(testCalendar1);
    assertEquals(1, calendarManager.getCalendarManagerStore().size());

    Calendar duplicateCalendar =
        new CalendarImpl(testCalendar1.getCalendarName(), ZoneId.of("Asia/Tokyo"));
    calendarManager.saveCalendar(duplicateCalendar);

    assertEquals(1, calendarManager.getCalendarManagerStore().size());
    Calendar storedCalendar =
        calendarManager.getCalendarManagerStore().get(testCalendar1.getCalendarName());
    assertEquals(ZoneId.of("Asia/Tokyo"), storedCalendar.getCalendarTimeZone());
  }

  @Test
  public void testValidateCalendarNoDuplicates() {
    calendarManager.saveCalendar(testCalendar1);

    Calendar differentCalendar = new CalendarImpl("DifferentName", ZoneId.of("Australia/Sydney"));
    calendarManager.saveCalendar(differentCalendar);

    assertEquals(2, calendarManager.getCalendarManagerStore().size());
  }

  @Test
  public void testValidateCalendarWithDuplicate() {
    calendarManager.saveCalendar(testCalendar1);

    Calendar duplicateCalendar =
        new CalendarImpl(testCalendar1.getCalendarName(), ZoneId.of("Europe/Paris"));

    calendarManager.saveCalendar(duplicateCalendar);

    assertEquals(1, calendarManager.getCalendarManagerStore().size());
    Calendar stored =
        calendarManager.getCalendarManagerStore().get(testCalendar1.getCalendarName());
    assertEquals(ZoneId.of("Europe/Paris"), stored.getCalendarTimeZone());
  }

  @Test
  public void testValidateCalendarLoopIteration() {
    Calendar cal1 = new CalendarImpl("Calendar1", ZoneId.of("America/New_York"));
    Calendar cal2 = new CalendarImpl("Calendar2", ZoneId.of("Europe/London"));
    Calendar cal3 = new CalendarImpl("Calendar3", ZoneId.of("Asia/Tokyo"));

    calendarManager.saveCalendar(cal1);
    calendarManager.saveCalendar(cal2);
    calendarManager.saveCalendar(cal3);

    assertEquals(3, calendarManager.getCalendarManagerStore().size());

    Calendar duplicateCal2 = new CalendarImpl("Calendar2", ZoneId.of("America/Los_Angeles"));
    calendarManager.saveCalendar(duplicateCal2);

    assertEquals(3, calendarManager.getCalendarManagerStore().size());
    Calendar stored = calendarManager.getCalendarManagerStore().get("Calendar2");
    assertEquals(ZoneId.of("America/Los_Angeles"), stored.getCalendarTimeZone());
  }

  @Test
  public void testSaveCalendarAfterException() {
    calendarManager.saveCalendar(testCalendar1);
    assertEquals(1, calendarManager.getCalendarManagerStore().size());

    Calendar duplicateCalendar =
        new CalendarImpl(testCalendar1.getCalendarName(), ZoneId.of("Pacific/Auckland"));
    calendarManager.saveCalendar(duplicateCalendar);

    assertEquals(1, calendarManager.getCalendarManagerStore().size());
  }

  @Test
  public void testCompleteWorkflow() {
    assertNull(calendarManager.getCurrentCalendarName());
    assertEquals(0, calendarManager.getCalendarManagerStore().size());

    Calendar cal1 = calendarManager.createCalendar("WorkCalendar", "America/New_York");
    Calendar cal2 = calendarManager.createCalendar("PersonalCalendar", "Europe/London");

    assertNotNull(cal1);
    assertNotNull(cal2);

    calendarManager.saveCalendar(cal1);
    calendarManager.saveCalendar(cal2);

    assertEquals(2, calendarManager.getCalendarManagerStore().size());

    calendarManager.setCurrentCalendarNameAs("WorkCalendar");
    assertEquals("WorkCalendar", calendarManager.getCurrentCalendarName());

    Calendar retrieved1 = calendarManager.getCalendar("WorkCalendar");
    Calendar retrieved2 = calendarManager.getCalendar("PersonalCalendar");

    assertSame(cal1, retrieved1);
    assertSame(cal2, retrieved2);

    assertNull(calendarManager.getCalendar("NonExistent"));

    assertNull(calendarManager.createCalendar("BadCalendar", "Invalid/Zone"));
  }

  @Test
  public void testCreateCalendarCatchBlockExecution() {
    Calendar result1 = calendarManager.createCalendar("Test", "BadZone");
    Calendar result2 = calendarManager.createCalendar(null, "America/New_York");
    Calendar result3 = calendarManager.createCalendar("Test", null);

    assertNull(result1);
    assertNull(result2);
    assertNull(result3);
  }

  @Test
  public void testValidateCalendarIteratesAllEntries() {
    Calendar cal1 = new CalendarImpl("First", ZoneId.of("America/New_York"));
    Calendar cal2 = new CalendarImpl("Second", ZoneId.of("Europe/London"));
    Calendar cal3 = new CalendarImpl("Third", ZoneId.of("Asia/Shanghai"));

    calendarManager.saveCalendar(cal1);
    calendarManager.saveCalendar(cal2);
    calendarManager.saveCalendar(cal3);

    Calendar duplicateThird = new CalendarImpl("Third", ZoneId.of("Australia/Melbourne"));
    calendarManager.saveCalendar(duplicateThird);

    assertEquals(3, calendarManager.getCalendarManagerStore().size());
  }
}