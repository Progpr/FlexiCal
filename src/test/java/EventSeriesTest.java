import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import calendar.model.modelimplementations.CalendarImpl;
import calendar.model.modelimplementations.EventSeriesImpl;
import calendar.model.modelinterfaces.Event;
import calendar.model.modelinterfaces.EventSeries;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive unit test class for EventSeriesImpl without Mockito.
 * Tests every line and branch of code for 100% coverage.
 */
public class EventSeriesTest {

  private CalendarImpl testCalendar;
  private Event testEvent;
  private EventSeriesImpl eventSeries;
  private LocalDate startDate;
  private LocalDate endDate;
  private List<DayOfWeek> repeatDays;

  /**
   * set up to test Event series implementation.
   */
  @Before
  public void setUp() {
    testCalendar = new CalendarImpl("TestCalendar", ZoneId.of("America/New_York"));
    startDate = LocalDate.of(2025, 1, 1);
    endDate = LocalDate.of(2025, 1, 31);
    repeatDays = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
  }

  @Test
  public void testConstructorAllParameters() {
    String seriesId = "test-series-123";
    int occurrences = 10;

    eventSeries = new EventSeriesImpl(seriesId, occurrences, startDate, endDate, repeatDays);

    assertEquals(seriesId, eventSeries.getEventSeriesId());
    assertEquals(occurrences, eventSeries.getOccurrences());
    assertEquals(startDate, eventSeries.getFirstDate());
    assertEquals(endDate, eventSeries.getLastDate());
    assertEquals(repeatDays, eventSeries.getRepeatDays());
  }

  @Test
  public void testConstructorWithNulls() {
    eventSeries = new EventSeriesImpl(null, 0, null, null, null);

    assertNull(eventSeries.getEventSeriesId());
    assertEquals(0, eventSeries.getOccurrences());
    assertNull(eventSeries.getFirstDate());
    assertNull(eventSeries.getLastDate());
    assertNull(eventSeries.getRepeatDays());
  }

  @Test
  public void testGetOccurrences() {
    int occurrences = 5;
    eventSeries = new EventSeriesImpl("id", occurrences, null, null, null);
    assertEquals(occurrences, eventSeries.getOccurrences());
  }

  @Test
  public void testGetEventSeriesId() {
    String seriesId = "series-456";
    eventSeries = new EventSeriesImpl(seriesId, 0, null, null, null);
    assertEquals(seriesId, eventSeries.getEventSeriesId());
  }

  @Test
  public void testGetSubject() {
    eventSeries = new EventSeriesImpl("id", 0, null, null, null);
    assertEquals("", eventSeries.getSubject());
  }

  @Test
  public void testGetFirstDate() {
    eventSeries = new EventSeriesImpl("id", 0, startDate, null, null);
    assertEquals(startDate, eventSeries.getFirstDate());
  }

  @Test
  public void testGetLastDate() {
    eventSeries = new EventSeriesImpl("id", 0, null, endDate, null);
    assertEquals(endDate, eventSeries.getLastDate());
  }

  @Test
  public void testGetEventSeriesBuilder() {
    EventSeriesImpl.EventSeriesBuilder builder = EventSeriesImpl.getEventSeriesBuilder();
    assertNotNull(builder);
    assertTrue(builder instanceof EventSeriesImpl.EventSeriesBuilder);
  }

  @Test
  public void testEventSeriesBuilderConstructor() {
    EventSeriesImpl.EventSeriesBuilder builder = new EventSeriesImpl.EventSeriesBuilder();

    assertNotNull(builder.eventSeriesId);
    assertEquals(0, builder.occurrences);
    assertNull(builder.startDate);
    assertNull(builder.endDate);
    assertNotNull(builder.repeatDays);
    assertEquals(0, builder.repeatDays.size());
  }

  @Test
  public void testBuilderSetOccurrences() {
    EventSeriesImpl.EventSeriesBuilder builder = new EventSeriesImpl.EventSeriesBuilder();
    EventSeriesImpl.EventSeriesBuilder result = builder.setOccurrences(7);

    assertSame(builder, result);
    assertEquals(7, builder.occurrences);
  }

  @Test
  public void testBuilderSetStartDate() {
    EventSeriesImpl.EventSeriesBuilder builder = new EventSeriesImpl.EventSeriesBuilder();
    EventSeriesImpl.EventSeriesBuilder result = builder.setStartDate(startDate);

    assertSame(builder, result);
    assertEquals(startDate, builder.startDate);
  }

  @Test
  public void testBuilderSetEndDate() {
    EventSeriesImpl.EventSeriesBuilder builder = new EventSeriesImpl.EventSeriesBuilder();
    EventSeriesImpl.EventSeriesBuilder result = builder.setEndDate(endDate);

    assertSame(builder, result);
    assertEquals(endDate, builder.endDate);
  }

  @Test
  public void testBuilderSetRepeatDays() {
    EventSeriesImpl.EventSeriesBuilder builder = new EventSeriesImpl.EventSeriesBuilder();
    EventSeriesImpl.EventSeriesBuilder result = builder.setRepeatDays(repeatDays);

    assertSame(builder, result);
    assertEquals(repeatDays, builder.repeatDays);
  }

  @Test
  public void testBuilderBuild() {
    EventSeriesImpl.EventSeriesBuilder builder = new EventSeriesImpl.EventSeriesBuilder();
    builder.setOccurrences(5)
        .setStartDate(startDate)
        .setEndDate(endDate)
        .setRepeatDays(repeatDays);

    EventSeries series = builder.build();

    assertNotNull(series);
    assertEquals(5, series.getOccurrences());
    assertEquals(startDate, series.getFirstDate());
    assertEquals(endDate, series.getLastDate());
  }

  @Test
  public void testCreateCopyOfEventOnDateAllFieldsNotNull() {
    LocalDate newDate = LocalDate.of(2025, 1, 15);
    LocalTime startTime = LocalTime.of(9, 0);
    LocalTime endTime = LocalTime.of(10, 0);
    String subject = "Test Event";
    String seriesId = "series-789";

    testEvent = testCalendar.createEvent(subject,
        LocalDate.now().atTime(startTime),
        LocalDate.now().atTime(endTime),
        seriesId, null, null, null);

    Event copiedEvent = EventSeriesImpl.createCopyOfEventOnDate(testEvent, newDate);

    assertNotNull(copiedEvent);
    assertEquals(subject, copiedEvent.getSubject());
    assertEquals(newDate, copiedEvent.getStartDate());
    assertEquals(newDate, copiedEvent.getEndDate());
    assertEquals(startTime, copiedEvent.getStartTime());
    assertEquals(endTime, copiedEvent.getEndTime());
    assertEquals(seriesId, copiedEvent.getEventSeriesId());
  }

  @Test
  public void testCreateCopyOfEventOnDateWithNullSubject() {
    testEvent = testCalendar.createEvent(null,
        LocalDate.now().atTime(9, 0),
        LocalDate.now().atTime(10, 0),
        "id", null, null, null);

    Event copiedEvent = EventSeriesImpl.createCopyOfEventOnDate(testEvent, LocalDate.now());
    assertNotNull(copiedEvent);
  }

  @Test
  public void testCreateCopyOfEventOnDateWithNullDate() {
    testEvent = testCalendar.createEvent("Subject",
        LocalDate.now().atTime(9, 0),
        LocalDate.now().atTime(10, 0),
        "id", null, null, null);

    Event copiedEvent = EventSeriesImpl.createCopyOfEventOnDate(testEvent, null);
    assertNotNull(copiedEvent);
  }

  @Test
  public void testCreateCopyOfEventOnDateWithNullEndTime() {
    testEvent = testCalendar.createEvent("Subject",
        LocalDate.now().atTime(9, 0),
        null,
        "id", null, null, null);

    Event copiedEvent = EventSeriesImpl.createCopyOfEventOnDate(testEvent, LocalDate.now());
    assertNotNull(copiedEvent);
  }

  @Test
  public void testCreateCopyOfEventOnDateWithNullSeriesId() {
    testEvent = testCalendar.createEvent("Subject",
        LocalDate.now().atTime(9, 0),
        LocalDate.now().atTime(10, 0),
        null, null, null, null);

    Event copiedEvent = EventSeriesImpl.createCopyOfEventOnDate(testEvent, LocalDate.now());
    assertNotNull(copiedEvent);
  }

  @Test
  public void testConstructSeriesEventsWithOccurrencesHappyPath() {
    int occurrences = 3;
    LocalDate eventStartDate = LocalDate.of(2025, 1, 1); // Wednesday
    eventSeries = new EventSeriesImpl("series-id", occurrences, null, null,
        Arrays.asList(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));

    testEvent = testCalendar.createEvent("Test Event",
        eventStartDate.atTime(9, 0),
        eventStartDate.atTime(10, 0),
        "series-id", null, null, null);

    testCalendar.getCalendarStore().clear();

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertEquals(2, testCalendar.getCalendarStore().size());

    List<LocalDate> expectedDates = Arrays.asList(
        LocalDate.of(2025, 1, 3),
        LocalDate.of(2025, 1, 8)
    );

    for (Event e : testCalendar.getCalendarStore().values()) {
      assertTrue(expectedDates.contains(e.getStartDate()));
    }
  }

  @Test
  public void testConstructSeriesEventsWithOccurrencesSkipNonRepeatDays() {
    int occurrences = 2;
    LocalDate eventStartDate = LocalDate.of(2025, 1, 1); // Wednesday
    eventSeries = new EventSeriesImpl("series-id", occurrences, null, null,
        Arrays.asList(DayOfWeek.MONDAY));

    testEvent = testCalendar.createEvent("Test Event",
        eventStartDate.atTime(9, 0),
        eventStartDate.atTime(10, 0),
        "series-id", null, null, null);

    testCalendar.getCalendarStore().clear();

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertEquals(1, testCalendar.getCalendarStore().size());

    Event createdEvent = testCalendar.getCalendarStore().values().iterator().next();
    assertEquals(LocalDate.of(2025, 1, 6), createdEvent.getStartDate());
  }

  @Test
  public void testConstructSeriesEventsWithOccurrencesHandleException() {
    int occurrences = 3;
    LocalDate eventStartDate = LocalDate.of(2025, 1, 1);
    eventSeries = new EventSeriesImpl("series-id", occurrences, null, null,
        Arrays.asList(DayOfWeek.FRIDAY));

    testEvent = testCalendar.createEvent("Test Event",
        eventStartDate.atTime(9, 0),
        eventStartDate.atTime(10, 0),
        "series-id", null, null, null);

    Event duplicate = testCalendar.createEvent("Test Event",
        LocalDate.of(2025, 1, 3).atTime(9, 0),
        LocalDate.of(2025, 1, 3).atTime(10, 0),
        "series-id", null, null, null);
    testCalendar.saveEvent(duplicate);

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertTrue(testCalendar.getCalendarStore().size() >= 1);
  }

  @Test
  public void testConstructSeriesEventsWithEndDateHappyPath() {
    LocalDate eventStartDate = LocalDate.of(2025, 1, 1); // Wednesday
    LocalDate seriesEndDate = LocalDate.of(2025, 1, 10); // Friday
    eventSeries = new EventSeriesImpl("series-id", 0, null, seriesEndDate,
        Arrays.asList(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));

    testEvent = testCalendar.createEvent("Test Event",
        eventStartDate.atTime(9, 0),
        eventStartDate.atTime(10, 0),
        "series-id", null, null, null);

    testCalendar.getCalendarStore().clear();

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertEquals(3, testCalendar.getCalendarStore().size());

    for (Event e : testCalendar.getCalendarStore().values()) {
      assertTrue(!e.getStartDate().isAfter(seriesEndDate));
    }
  }

  @Test
  public void testConstructSeriesEventsWithEndDateAfterLastDate() {
    LocalDate eventStartDate = LocalDate.of(2025, 1, 1);
    LocalDate seriesEndDate = LocalDate.of(2025, 1, 2);
    eventSeries = new EventSeriesImpl("series-id", 0, null, seriesEndDate,
        Arrays.asList(DayOfWeek.THURSDAY));

    testEvent = testCalendar.createEvent("Test Event",
        eventStartDate.atTime(9, 0),
        eventStartDate.atTime(10, 0),
        "series-id", null, null, null);

    testCalendar.getCalendarStore().clear();

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertEquals(1, testCalendar.getCalendarStore().size());
  }

  @Test
  public void testConstructSeriesEventsWithEndDateSkipNonRepeatDays() {
    LocalDate eventStartDate = LocalDate.of(2025, 1, 1);
    LocalDate seriesEndDate = LocalDate.of(2025, 1, 5);
    eventSeries = new EventSeriesImpl("series-id", 0, null, seriesEndDate,
        Arrays.asList(DayOfWeek.MONDAY));

    testEvent = testCalendar.createEvent("Test Event",
        eventStartDate.atTime(9, 0),
        eventStartDate.atTime(10, 0),
        "series-id", null, null, null);

    testCalendar.getCalendarStore().clear();

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertEquals(0, testCalendar.getCalendarStore().size());
  }

  @Test
  public void testConstructSeriesEventsWithEndDateHandleException() {
    LocalDate eventStartDate = LocalDate.of(2025, 1, 1);
    LocalDate seriesEndDate = LocalDate.of(2025, 1, 10);
    eventSeries = new EventSeriesImpl("series-id", 0, null, seriesEndDate,
        Arrays.asList(DayOfWeek.FRIDAY));

    testEvent = testCalendar.createEvent("Test Event",
        eventStartDate.atTime(9, 0),
        eventStartDate.atTime(10, 0),
        "series-id", null, null, null);

    Event duplicate = testCalendar.createEvent("Test Event",
        LocalDate.of(2025, 1, 3).atTime(9, 0),
        LocalDate.of(2025, 1, 3).atTime(10, 0),
        "series-id", null, null, null);
    testCalendar.saveEvent(duplicate);

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertTrue(testCalendar.getCalendarStore().size() >= 1);
  }

  @Test
  public void testConstructSeriesEventsNoOccurrencesNoEndDate() {
    eventSeries = new EventSeriesImpl("series-id", 0, null, null, repeatDays);

    testEvent = testCalendar.createEvent("Test Event",
        startDate.atTime(9, 0),
        startDate.atTime(10, 0),
        "series-id", null, null, null);

    testCalendar.getCalendarStore().clear();

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertEquals(0, testCalendar.getCalendarStore().size());
  }

  @Test
  public void testConstructSeriesEventsZeroOccurrences() {
    eventSeries = new EventSeriesImpl("series-id", 0, null, null, repeatDays);

    testEvent = testCalendar.createEvent("Test Event",
        startDate.atTime(9, 0),
        startDate.atTime(10, 0),
        "series-id", null, null, null);

    testCalendar.getCalendarStore().clear();

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertEquals(0, testCalendar.getCalendarStore().size());
  }

  @Test
  public void testConstructSeriesEventsNegativeOccurrences() {
    eventSeries = new EventSeriesImpl("series-id", -1, null, null, repeatDays);

    testEvent = testCalendar.createEvent("Test Event",
        startDate.atTime(9, 0),
        startDate.atTime(10, 0),
        "series-id", null, null, null);

    testCalendar.getCalendarStore().clear();

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertEquals(0, testCalendar.getCalendarStore().size());
  }

  @Test
  public void testConstructSeriesEventsWithNullRepeatDays() {
    eventSeries = new EventSeriesImpl("series-id", 5, null, null, null);

    testEvent = testCalendar.createEvent("Test Event",
        startDate.atTime(9, 0),
        startDate.atTime(10, 0),
        "series-id", null, null, null);

    try {
      eventSeries.constructSeriesEvents(testEvent, testCalendar);
      fail("Should throw NullPointerException");
    } catch (NullPointerException e) {
      // Expected
    }
  }

  @Test
  public void testConstructSeriesEventsOccurrencesReachExactCount() {
    int occurrences = 2;
    LocalDate eventStartDate = LocalDate.of(2025, 1, 1);
    eventSeries = new EventSeriesImpl("series-id", occurrences, null, null,
        Arrays.asList(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));

    testEvent = testCalendar.createEvent("Test Event",
        eventStartDate.atTime(9, 0),
        eventStartDate.atTime(10, 0),
        "series-id", null, null, null);

    testCalendar.getCalendarStore().clear();

    eventSeries.constructSeriesEvents(testEvent, testCalendar);

    assertEquals(1, testCalendar.getCalendarStore().size());
  }

  @Test
  public void testConstructSeriesEventsAllExceptionsInEndDate() {
    LocalDate eventStartDate = LocalDate.of(2025, 1, 1);
    LocalDate seriesEndDate = LocalDate.of(2025, 1, 10);
    eventSeries = new EventSeriesImpl("series-id", 0, null, seriesEndDate,
        Arrays.asList(DayOfWeek.FRIDAY));

    testEvent = testCalendar.createEvent("Test Event",
        eventStartDate.atTime(9, 0),
        eventStartDate.atTime(10, 0),
        "series-id", null, null, null);

    Event dup1 = testCalendar.createEvent("Test Event",
        LocalDate.of(2025, 1, 3).atTime(9, 0),
        LocalDate.of(2025, 1, 3).atTime(10, 0),
        "series-id", null, null, null);
    Event dup2 = testCalendar.createEvent("Test Event",
        LocalDate.of(2025, 1, 10).atTime(9, 0),
        LocalDate.of(2025, 1, 10).atTime(10, 0),
        "series-id", null, null, null);

    testCalendar.saveEvent(dup1);
    testCalendar.saveEvent(dup2);

    int sizeBefore = testCalendar.getCalendarStore().size();
    eventSeries.constructSeriesEvents(testEvent, testCalendar);
    int sizeAfter = testCalendar.getCalendarStore().size();

    assertEquals(sizeBefore, sizeAfter);
  }
}