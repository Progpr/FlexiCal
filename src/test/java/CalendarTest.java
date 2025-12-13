import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import calendar.model.modelimplementations.CalendarImpl;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.Event;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.management.openmbean.KeyAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive test class for CalendarImpl covering Assignment 5 requirements.
 * Tests multiple calendars, timezones, copy operations, and export functionality.
 */
public class CalendarTest {

  private Calendar personalCalendar;
  private Calendar workCalendar;
  private Calendar schoolCalendar;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;

  /**
   * Set up test calendars with different timezones.
   */
  @Before
  public void setUp() {
    personalCalendar = new CalendarImpl("Personal", ZoneId.of("America/New_York"));
    workCalendar = new CalendarImpl("Work", ZoneId.of("America/Los_Angeles"));
    schoolCalendar = new CalendarImpl("School", ZoneId.of("Europe/London"));
    startDateTime = LocalDateTime.of(2025, 5, 15, 10, 0);
    endDateTime = LocalDateTime.of(2025, 5, 15, 11, 0);
  }

  @Test
  public void testCalendarCreationWithNameAndTimezone() {
    assertNotNull(personalCalendar);
    assertEquals("Personal", personalCalendar.getCalendarName());
    assertEquals(ZoneId.of("America/New_York"), personalCalendar.getCalendarTimeZone());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCalendarCreationEmptyName() {
    new CalendarImpl("", ZoneId.of("America/New_York"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCalendarCreationNullTimezone() {
    new CalendarImpl("Test", null);
  }

  @Test
  public void testModifyCalendarName() {
    Calendar modified = personalCalendar.modifyName("Personal_Updated");
    assertEquals("Personal_Updated", modified.getCalendarName());
    assertSame(personalCalendar, modified);
  }

  @Test
  public void testModifyCalendarTimezone() {
    Calendar modified = personalCalendar.modifyTimezone(ZoneId.of("Asia/Tokyo"));
    assertEquals(ZoneId.of("Asia/Tokyo"), modified.getCalendarTimeZone());
    assertSame(personalCalendar, modified);
  }

  @Test
  public void testCalendarToString() {
    String result = personalCalendar.toString();
    assertTrue(result.contains("Personal"));
    assertTrue(result.contains("America/New_York"));
  }

  @Test
  public void testCreateEventValidEvent() {
    Event event =
        personalCalendar.createEvent("Meeting", startDateTime, endDateTime, null, null, null, null);

    assertNotNull(event);
    assertEquals("Meeting", event.getSubject());
    assertEquals(LocalDate.of(2025, 5, 15), event.getStartDate());
    assertEquals(LocalDate.of(2025, 5, 15), event.getEndDate());
  }

  @Test
  public void testCreateEventWithSeriesId() {
    Event event =
        personalCalendar.createEvent("Meeting", startDateTime, endDateTime, "SERIES-123", null,
            null, null);

    assertNotNull(event);
    assertEquals("SERIES-123", event.getEventSeriesId());
  }

  @Test
  public void testCreateEventMultiDayEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 5, 15, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 5, 17, 17, 0);

    Event event = personalCalendar.createEvent("Conference", start, end, null, null, null, null);

    assertNotNull(event);
    assertEquals(LocalDate.of(2025, 5, 15), event.getStartDate());
    assertEquals(LocalDate.of(2025, 5, 17), event.getEndDate());
  }

  @Test
  public void testCreateEventAllDay() {
    Event event = personalCalendar.createEvent("All Day Event", null, null, null, null, null, null);

    assertNotNull(event);
    assertEquals(LocalTime.of(8, 0), event.getStartTime());
    assertEquals(LocalTime.of(17, 0), event.getEndTime());
  }

  @Test
  public void testCreateSeries() {

    final Event event = personalCalendar.createEvent(
        "Daily Standup",
        LocalDateTime.of(2025, 6, 1, 9, 0),
        LocalDateTime.of(2025, 6, 1, 9, 30),
        null, null, null, null
    );
    List<DayOfWeek> repeatDays = new ArrayList<>();
    repeatDays.add(DayOfWeek.MONDAY);
    repeatDays.add(DayOfWeek.WEDNESDAY);
    repeatDays.add(DayOfWeek.FRIDAY);

    personalCalendar.createSeries(event, 10, repeatDays, null);

    List<Event> events = personalCalendar.getEventsForDate(LocalDate.of(2025, 6, 2));
    assertTrue(events.size() > 0);
  }

  @Test
  public void testCreateSeriesWithEndDate() {
    Event event = workCalendar.createEvent(
        "Team Meeting",
        LocalDateTime.of(2025, 6, 1, 14, 0),
        LocalDateTime.of(2025, 6, 1, 15, 0),
        null, null, null, null
    );

    List<DayOfWeek> repeatDays = new ArrayList<>();
    repeatDays.add(DayOfWeek.TUESDAY);
    repeatDays.add(DayOfWeek.THURSDAY);

    workCalendar.createSeries(event, 0, repeatDays, LocalDate.of(2025, 6, 30));

    assertNotNull(workCalendar.getCalendarStore());
  }

  @Test
  public void testSaveEventValidEvent() {
    Event event =
        personalCalendar.createEvent("Meeting", startDateTime, endDateTime, null, null, null, null);
    personalCalendar.saveEvent(event);

    Event retrieved = personalCalendar.getEvent("Meeting",
        LocalDate.of(2025, 5, 15),
        LocalDate.of(2025, 5, 15),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0));

    assertNotNull(retrieved);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testSaveEventStartDateAfterEndDate() {
    LocalDateTime start = LocalDateTime.of(2025, 5, 20, 10, 0);
    LocalDateTime end = LocalDateTime.of(2025, 5, 15, 11, 0);

    Event event = personalCalendar.createEvent("Meeting", start, end, null, null, null, null);
    personalCalendar.saveEvent(event);
  }

  @Test
  public void testCopyEventToSameCalendar() {
    Event original = personalCalendar.createEvent(
        "Meeting",
        startDateTime,
        endDateTime,
        null, null, null, null
    );
    personalCalendar.saveEvent(original);

    LocalDateTime targetDateTime = LocalDateTime.of(2025, 6, 1, 14, 0);
    Event copied = personalCalendar.copyEvent(original, personalCalendar, targetDateTime);

    assertNotNull(copied);
    assertEquals("Meeting_copy", copied.getSubject());
    assertEquals(LocalDate.of(2025, 6, 1), copied.getStartDate());
    assertEquals(LocalTime.of(14, 0), copied.getStartTime());
  }

  @Test
  public void testCopyEventWithTimezoneConversion() {
    Event original = personalCalendar.createEvent(
        "EST Meeting",
        LocalDateTime.of(2025, 5, 15, 15, 0),
        LocalDateTime.of(2025, 5, 15, 16, 0),
        null, null, null, null
    );
    personalCalendar.saveEvent(original);

    LocalDateTime targetDateTime = LocalDateTime.of(2025, 6, 1, 15, 0);
    Event copied = personalCalendar.copyEvent(original, workCalendar, targetDateTime);

    assertNotNull(copied);
    assertEquals(LocalTime.of(12, 0), copied.getStartTime());
  }

  @Test
  public void testCopyEventFromSeries() {
    Event seriesEvent = personalCalendar.createEvent(
        "Series Event",
        startDateTime,
        endDateTime,
        "SERIES-001", null, null, null
    );
    personalCalendar.saveEvent(seriesEvent);

    LocalDateTime targetDateTime = LocalDateTime.of(2025, 6, 10, 10, 0);
    Event copied = personalCalendar.copyEvent(seriesEvent, workCalendar, targetDateTime);

    assertNotNull(copied);
    assertNull(copied.getEventSeriesId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventNullEvent() {
    personalCalendar.copyEvent(null, workCalendar, LocalDateTime.now());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventNullTargetCalendar() {
    Event event =
        personalCalendar.createEvent("Test", startDateTime, endDateTime, null, null, null, null);
    personalCalendar.copyEvent(event, null, LocalDateTime.now());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventNullTargetDateTime() {
    Event event =
        personalCalendar.createEvent("Test", startDateTime, endDateTime, null, null, null, null);
    personalCalendar.copyEvent(event, workCalendar, null);
  }

  @Test
  public void testCopyEventsOnDate() {
    Event event1 = personalCalendar.createEvent(
        "Morning Meeting",
        LocalDateTime.of(2025, 5, 15, 9, 0),
        LocalDateTime.of(2025, 5, 15, 10, 0),
        null, null, null, null
    );
    Event event2 = personalCalendar.createEvent(
        "Lunch",
        LocalDateTime.of(2025, 5, 15, 12, 0),
        LocalDateTime.of(2025, 5, 15, 13, 0),
        null, null, null, null
    );

    personalCalendar.saveEvent(event1);
    personalCalendar.saveEvent(event2);

    List<Event> copied = personalCalendar.copyEventsOnDate(
        LocalDate.of(2025, 5, 15),
        workCalendar,
        LocalDate.of(2025, 6, 20)
    );

    assertEquals(2, copied.size());

    List<Event> workEvents = workCalendar.getEventsForDate(LocalDate.of(2025, 6, 20));
    assertEquals(2, workEvents.size());
  }

  @Test
  public void testCopyEventsOnDateWithSeries() {
    Event standalone = personalCalendar.createEvent(
        "Standalone",
        LocalDateTime.of(2025, 5, 15, 9, 0),
        LocalDateTime.of(2025, 5, 15, 10, 0),
        null, null, null, null
    );
    Event series1 = personalCalendar.createEvent(
        "Series Event 1",
        LocalDateTime.of(2025, 5, 15, 11, 0),
        LocalDateTime.of(2025, 5, 15, 12, 0),
        "SERIES-001", null, null, null
    );
    Event series2 = personalCalendar.createEvent(
        "Series Event 2",
        LocalDateTime.of(2025, 5, 15, 14, 0),
        LocalDateTime.of(2025, 5, 15, 15, 0),
        "SERIES-001", null, null, null
    );

    personalCalendar.saveEvent(standalone);
    personalCalendar.saveEvent(series1);
    personalCalendar.saveEvent(series2);

    List<Event> copied = personalCalendar.copyEventsOnDate(
        LocalDate.of(2025, 5, 15),
        workCalendar,
        LocalDate.of(2025, 6, 25)
    );

    assertEquals(3, copied.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsOnDateNullSourceDate() {
    personalCalendar.copyEventsOnDate(null, workCalendar, LocalDate.of(2025, 6, 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsOnDateNullTargetCalendar() {
    personalCalendar.copyEventsOnDate(LocalDate.of(2025, 5, 15), null, LocalDate.of(2025, 6, 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsOnDateNullTargetDate() {
    personalCalendar.copyEventsOnDate(LocalDate.of(2025, 5, 15), workCalendar, null);
  }

  @Test
  public void testCopyEventsBetween() {
    Event event1 = personalCalendar.createEvent(
        "Day 1 Event",
        LocalDateTime.of(2025, 5, 15, 10, 0),
        LocalDateTime.of(2025, 5, 15, 11, 0),
        null, null, null, null
    );
    Event event2 = personalCalendar.createEvent(
        "Day 2 Event",
        LocalDateTime.of(2025, 5, 16, 14, 0),
        LocalDateTime.of(2025, 5, 16, 15, 0),
        null, null, null, null
    );
    Event event3 = personalCalendar.createEvent(
        "Day 3 Event",
        LocalDateTime.of(2025, 5, 17, 9, 0),
        LocalDateTime.of(2025, 5, 17, 10, 0),
        null, null, null, null
    );

    personalCalendar.saveEvent(event1);
    personalCalendar.saveEvent(event2);
    personalCalendar.saveEvent(event3);

    List<Event> copied = personalCalendar.copyEventsBetween(
        LocalDate.of(2025, 5, 15),
        LocalDate.of(2025, 5, 17),
        schoolCalendar,
        LocalDate.of(2025, 7, 1)
    );

    assertEquals(3, copied.size());

    List<Event> day1Events = schoolCalendar.getEventsForDate(LocalDate.of(2025, 7, 1));
    List<Event> day2Events = schoolCalendar.getEventsForDate(LocalDate.of(2025, 7, 2));
    List<Event> day3Events = schoolCalendar.getEventsForDate(LocalDate.of(2025, 7, 3));

    assertEquals(1, day1Events.size());
    assertEquals(1, day2Events.size());
    assertEquals(1, day3Events.size());
  }

  @Test
  public void testCopyEventsBetweenPartialSeriesOverlap() {
    Event series1 = personalCalendar.createEvent(
        "Series Event",
        LocalDateTime.of(2025, 5, 14, 10, 0),
        LocalDateTime.of(2025, 5, 14, 11, 0),
        "SERIES-001", null, null, null
    );
    Event series2 = personalCalendar.createEvent(
        "Series Event",
        LocalDateTime.of(2025, 5, 15, 10, 0),
        LocalDateTime.of(2025, 5, 15, 11, 0),
        "SERIES-001", null, null, null
    );
    Event series3 = personalCalendar.createEvent(
        "Series Event",
        LocalDateTime.of(2025, 5, 16, 10, 0),
        LocalDateTime.of(2025, 5, 16, 11, 0),
        "SERIES-001", null, null, null
    );
    Event series4 = personalCalendar.createEvent(
        "Series Event",
        LocalDateTime.of(2025, 5, 17, 10, 0),
        LocalDateTime.of(2025, 5, 17, 11, 0),
        "SERIES-001", null, null, null
    );

    personalCalendar.saveEvent(series1);
    personalCalendar.saveEvent(series2);
    personalCalendar.saveEvent(series3);
    personalCalendar.saveEvent(series4);

    List<Event> copied = personalCalendar.copyEventsBetween(
        LocalDate.of(2025, 5, 15),
        LocalDate.of(2025, 5, 16),
        workCalendar,
        LocalDate.of(2025, 6, 1)
    );

    assertEquals(2, copied.size());

    String firstSeriesId = copied.get(0).getEventSeriesId();
    String secondSeriesId = copied.get(1).getEventSeriesId();
    assertEquals(firstSeriesId, secondSeriesId);
    assertNotEquals("SERIES-001", firstSeriesId);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenInvalidDateRange() {
    personalCalendar.copyEventsBetween(
        LocalDate.of(2025, 5, 20),
        LocalDate.of(2025, 5, 15),
        workCalendar,
        LocalDate.of(2025, 6, 1)
    );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenNullStartDate() {
    personalCalendar.copyEventsBetween(null, LocalDate.of(2025, 5, 20), workCalendar,
        LocalDate.of(2025, 6, 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenNullEndDate() {
    personalCalendar.copyEventsBetween(LocalDate.of(2025, 5, 15), null, workCalendar,
        LocalDate.of(2025, 6, 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenNullTargetCalendar() {
    personalCalendar.copyEventsBetween(LocalDate.of(2025, 5, 15), LocalDate.of(2025, 5, 20), null,
        LocalDate.of(2025, 6, 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsBetweenNullTargetStart() {
    personalCalendar.copyEventsBetween(LocalDate.of(2025, 5, 15), LocalDate.of(2025, 5, 20),
        workCalendar, null);
  }

  @Test
  public void testHasConflict() {
    Event event1 = personalCalendar.createEvent(
        "Meeting",
        LocalDateTime.of(2025, 5, 15, 10, 0),
        LocalDateTime.of(2025, 5, 15, 11, 0),
        null, null, null, null
    );
    personalCalendar.saveEvent(event1);

    Event conflicting = personalCalendar.createEvent(
        "Conflicting Meeting",
        LocalDateTime.of(2025, 5, 15, 10, 30),
        LocalDateTime.of(2025, 5, 15, 11, 30),
        null, null, null, null
    );

    assertTrue(personalCalendar.hasConflict(conflicting));
  }

  @Test
  public void testHasConflictNoOverlap() {
    Event event1 = personalCalendar.createEvent(
        "Morning Meeting",
        LocalDateTime.of(2025, 5, 15, 9, 0),
        LocalDateTime.of(2025, 5, 15, 10, 0),
        null, null, null, null
    );
    personalCalendar.saveEvent(event1);

    Event nonConflicting = personalCalendar.createEvent(
        "Afternoon Meeting",
        LocalDateTime.of(2025, 5, 15, 14, 0),
        LocalDateTime.of(2025, 5, 15, 15, 0),
        null, null, null, null
    );

    assertFalse(personalCalendar.hasConflict(nonConflicting));
  }

  @Test
  public void testHasConflictAdjacentEvents() {
    Event event1 = personalCalendar.createEvent(
        "First Meeting",
        LocalDateTime.of(2025, 5, 15, 10, 0),
        LocalDateTime.of(2025, 5, 15, 11, 0),
        null, null, null, null
    );
    personalCalendar.saveEvent(event1);

    Event adjacent = personalCalendar.createEvent(
        "Adjacent Meeting",
        LocalDateTime.of(2025, 5, 15, 11, 0),
        LocalDateTime.of(2025, 5, 15, 12, 0),
        null, null, null, null
    );

    assertFalse(personalCalendar.hasConflict(adjacent));
  }

  @Test
  public void testHasConflictNullEvent() {
    assertFalse(personalCalendar.hasConflict(null));
  }

  @Test
  public void testGetScheduleSingleDay() {
    Event event =
        personalCalendar.createEvent("Meeting", startDateTime, endDateTime, null, null, null, null);
    personalCalendar.saveEvent(event);

    List<Event> schedule = personalCalendar.getSchedule("2025-05-15T00:00", "2025-05-15T23:59");

    assertEquals(1, schedule.size());
    assertEquals("Meeting", schedule.get(0).getSubject());
  }

  @Test
  public void testGetScheduleMultipleEvents() {
    Event event1 = personalCalendar.createEvent("Meeting 1",
        LocalDateTime.of(2025, 5, 15, 10, 0),
        LocalDateTime.of(2025, 5, 15, 11, 0), null, null, null, null);
    Event event2 = personalCalendar.createEvent("Meeting 2",
        LocalDateTime.of(2025, 5, 15, 14, 0),
        LocalDateTime.of(2025, 5, 15, 15, 0), null, null, null, null);

    personalCalendar.saveEvent(event1);
    personalCalendar.saveEvent(event2);

    List<Event> schedule = personalCalendar.getSchedule("2025-05-15T00:00", "2025-05-15T23:59");

    assertEquals(2, schedule.size());
  }

  @Test
  public void testGetScheduleDateRange() {
    Event event1 = personalCalendar.createEvent("Day 1 Meeting",
        LocalDateTime.of(2025, 5, 15, 10, 0),
        LocalDateTime.of(2025, 5, 15, 11, 0), null, null, null, null);
    Event event2 = personalCalendar.createEvent("Day 2 Meeting",
        LocalDateTime.of(2025, 5, 16, 10, 0),
        LocalDateTime.of(2025, 5, 16, 11, 0), null, null, null, null);
    Event event3 = personalCalendar.createEvent("Day 3 Meeting",
        LocalDateTime.of(2025, 5, 17, 10, 0),
        LocalDateTime.of(2025, 5, 17, 11, 0), null, null, null, null);

    personalCalendar.saveEvent(event1);
    personalCalendar.saveEvent(event2);
    personalCalendar.saveEvent(event3);

    List<Event> schedule = personalCalendar.getSchedule("2025-05-15T00:00", "2025-05-17T23:59");

    assertEquals(3, schedule.size());
  }


  @Test
  public void testShowStatusBusy() {
    Event event =
        personalCalendar.createEvent("Meeting", startDateTime, endDateTime, null, null, null, null);
    personalCalendar.saveEvent(event);

    String status = personalCalendar.showStatus("2025-05-15T10:00");

    assertEquals("Busy", status);
  }

  @Test
  public void testShowStatusAvailable() {
    Event event =
        personalCalendar.createEvent("Meeting", startDateTime, endDateTime, null, null, null, null);
    personalCalendar.saveEvent(event);

    String status = personalCalendar.showStatus("2025-05-15T14:00");

    assertEquals("Available", status);
  }

  @Test
  public void testUpdateEventKey() {
    Event event =
        personalCalendar.createEvent("Original", startDateTime, endDateTime, null, null, null,
            null);
    personalCalendar.saveEvent(event);

    Event originalState =
        personalCalendar.createEvent("Original", startDateTime, endDateTime, null, null, null,
            null);

    event.modifySubject("Updated");
    personalCalendar.updateEventKey(originalState, event);

    Event retrieved = personalCalendar.getEvent("Updated",
        startDateTime.toLocalDate(),
        endDateTime.toLocalDate(),
        startDateTime.toLocalTime(),
        endDateTime.toLocalTime());

    assertNotNull(retrieved);
    assertEquals("Updated", retrieved.getSubject());

    Event oldRetrieved = personalCalendar.getEvent("Original",
        startDateTime.toLocalDate(),
        endDateTime.toLocalDate(),
        startDateTime.toLocalTime(),
        endDateTime.toLocalTime());

    assertNull(oldRetrieved);
  }


  @Test
  public void testMultipleCalendarsIndependence() {
    Event personalEvent = personalCalendar.createEvent(
        "Personal Event",
        startDateTime,
        endDateTime,
        null, null, null, null
    );
    personalCalendar.saveEvent(personalEvent);

    Event workEvent = workCalendar.createEvent(
        "Work Event",
        startDateTime,
        endDateTime,
        null, null, null, null
    );
    workCalendar.saveEvent(workEvent);

    List<Event> personalEvents = personalCalendar.getEventsForDate(LocalDate.of(2025, 5, 15));
    List<Event> workEvents = workCalendar.getEventsForDate(LocalDate.of(2025, 5, 15));

    assertEquals(1, personalEvents.size());
    assertEquals(1, workEvents.size());
    assertEquals("Personal Event", personalEvents.get(0).getSubject());
    assertEquals("Work Event", workEvents.get(0).getSubject());
  }

  @Test
  public void testDifferentTimezonesForCalendars() {
    assertEquals(ZoneId.of("America/New_York"), personalCalendar.getCalendarTimeZone());
    assertEquals(ZoneId.of("America/Los_Angeles"), workCalendar.getCalendarTimeZone());
    assertEquals(ZoneId.of("Europe/London"), schoolCalendar.getCalendarTimeZone());
  }
}