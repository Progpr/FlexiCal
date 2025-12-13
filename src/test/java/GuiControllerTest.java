import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import calendar.controller.GuiControllerFeatures;
import calendar.controller.GuiControllerHandlers;
import calendar.model.modelimplementations.CalendarImpl;
import calendar.model.modelimplementations.CalendarManagerImpl;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.model.modelinterfaces.Event;
import calendar.view.ViewInterface;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for GuiControllerHandlers.
 * Tests controller logic and interactions between controller, model, and view.
 * Uses a TestView implementation to verify view method calls without actual GUI rendering.
 */
public class GuiControllerTest {

  private GuiControllerHandlers controller;
  private CalendarManager calendarManager;
  private TestView testView;

  /**
   * Sets up test fixtures before each test.
   * Initializes calendar manager with a default calendar, test view, and controller.
   */

  @Before
  public void setUp() {
    calendarManager = new CalendarManagerImpl("TestCalendar", "America/New_York");
    testView = new TestView();
    controller = new GuiControllerHandlers(calendarManager);
    controller.setView(testView);
  }

  @Test
  public void testSetView() {
    TestView newView = new TestView();
    controller.setView(newView);
    assertTrue(newView.handlersAdded);
  }

  @Test
  public void testHandleNavigateCalendarClickedWithMultipleCalendars() {
    Calendar workCal = new CalendarImpl("Work", java.time.ZoneId.of("America/New_York"));
    Calendar personalCal = new CalendarImpl("Personal", java.time.ZoneId.of("America/Chicago"));
    calendarManager.saveCalendar(workCal);
    calendarManager.saveCalendar(personalCal);

    controller.handleNavigateCalendarClicked();

    assertNotNull(testView.shownCalendars);
    assertTrue(testView.shownCalendars.size() >= 2);
    assertTrue(testView.shownCalendars.contains("Work")
        ||
        testView.shownCalendars.contains("Personal"));
  }

  @Test
  public void testHandleNavigateCalendarClickedWithSingleCalendar() {
    controller.handleNavigateCalendarClicked();

    assertNotNull(testView.shownCalendars);
    assertEquals(1, testView.shownCalendars.size());
    assertTrue(testView.shownCalendars.contains("TestCalendar"));
  }

  @Test
  public void testHandleDateClickedWithEvents() {
    Calendar currentCal = calendarManager.getCalendar("TestCalendar");
    LocalDateTime start = LocalDateTime.of(2024, 11, 20, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 11, 20, 11, 0);
    Event event = currentCal.createEvent("Meeting", start, end, null,
        "Online", "Public", "Test meeting");
    currentCal.saveEvent(event);

    testView.currentDate = LocalDate.of(2024, 11, 20);
    controller.handleDateClicked();

    assertNotNull(testView.shownEvents);
    assertEquals(1, testView.shownEvents.size());
    assertEquals("Meeting", testView.shownEvents.get(0).getSubject());
  }

  @Test
  public void testHandleDateClickedWithNoEvents() {
    testView.currentDate = LocalDate.of(2024, 12, 25);
    controller.handleDateClicked();

    assertNotNull(testView.shownEvents);
    assertEquals(0, testView.shownEvents.size());
  }

  @Test
  public void testHandleCreateEventClicked() {
    controller.handleCreateEventClicked();
    assertTrue(testView.createEventDialogShown);
  }

  @Test
  public void testHandleDialogBoxCreateEventSingleEvent() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Team Meeting");
    params.put("startDate", "2024-11-20");
    params.put("endDate", "2024-11-20");
    params.put("startTime", "10:00");
    params.put("endTime", "11:00");
    params.put("location", "Online");
    params.put("status", "Public");
    params.put("description", "Weekly team sync");
    params.put("isRecurring", false);

    testView.currentDate = LocalDate.of(2024, 11, 20);
    controller.handleDialogBoxCreateEvent(params);

    Calendar currentCal = calendarManager.getCalendar("TestCalendar");
    List<Event> events = currentCal.getEventsForDate(LocalDate.of(2024, 11, 20));
    assertEquals(1, events.size());
    assertEquals("Team Meeting", events.get(0).getSubject());
  }

  @Test
  public void testHandleDialogBoxCreateEventRecurringWithOccurrences() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Standup");
    params.put("startDate", "2024-11-20");
    params.put("endDate", "2024-11-20");
    params.put("startTime", "09:00");
    params.put("endTime", "09:15");
    params.put("location", "Online");
    params.put("status", "Public");
    params.put("description", "Daily standup");
    params.put("isRecurring", true);
    params.put("numberOfTimes", 3);
    params.put("repeatsPattern", "MWF");

    testView.currentDate = LocalDate.of(2024, 11, 20);
    controller.handleDialogBoxCreateEvent(params);

    Calendar currentCal = calendarManager.getCalendar("TestCalendar");
    List<Event> events = currentCal.getEventsForDate(LocalDate.of(2024, 11, 20));
    assertTrue(events.size() >= 1);
  }

  @Test
  public void testHandleDialogBoxCreateEventRecurringWithLastDate() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Workout");
    params.put("startDate", "2024-11-20");
    params.put("endDate", "2024-11-20");
    params.put("startTime", "06:00");
    params.put("endTime", "07:00");
    params.put("location", "Physical");
    params.put("status", "Private");
    params.put("description", "Morning workout");
    params.put("isRecurring", true);
    params.put("numberOfTimes", 0);
    params.put("lastDate", "2024-11-25");
    params.put("repeatsPattern", "MTWRF");

    testView.currentDate = LocalDate.of(2024, 11, 20);
    controller.handleDialogBoxCreateEvent(params);

    Calendar currentCal = calendarManager.getCalendar("TestCalendar");
    List<Event> events = currentCal.getEventsForDate(LocalDate.of(2024, 11, 20));
    assertTrue(events.size() >= 1);
  }


  @Test
  public void testHandleDialogBoxCreateEventDuplicateEvent() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Meeting");
    params.put("startDate", "2024-11-20");
    params.put("endDate", "2024-11-20");
    params.put("startTime", "10:00");
    params.put("endTime", "11:00");
    params.put("location", "Online");
    params.put("status", "Public");
    params.put("description", "Test");
    params.put("isRecurring", false);

    testView.currentDate = LocalDate.of(2024, 11, 20);
    controller.handleDialogBoxCreateEvent(params);
    controller.handleDialogBoxCreateEvent(params);

    assertTrue(testView.eventExistsErrorShown);
  }

  @Test
  public void testHandleEditEventClickedSuccessfully() {
    Calendar currentCal = calendarManager.getCalendar("TestCalendar");
    LocalDateTime start = LocalDateTime.of(2024, 11, 20, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 11, 20, 11, 0);
    Event event = currentCal.createEvent("Meeting", start, end, null,
        "Online", "Public", "Test");
    currentCal.saveEvent(event);

    Map<String, Object> updatedData = new HashMap<>();
    updatedData.put("name", "Meeting");
    updatedData.put("startDate", "2024-11-20");
    updatedData.put("endDate", "2024-11-20");
    updatedData.put("startTime", "10:00");
    updatedData.put("endTime", "11:00");
    updatedData.put("location", "Physical");
    updatedData.put("status", "Private");
    updatedData.put("description", "Updated description");

    testView.currentDate = LocalDate.of(2024, 11, 20);
    controller.handleEditEventClicked(updatedData);

    Event editedEvent = currentCal.getEvent("Meeting",
        LocalDate.of(2024, 11, 20), LocalDate.of(2024, 11, 20),
        LocalTime.of(10, 0), LocalTime.of(11, 0));
    assertNotNull(editedEvent);
    assertEquals("Private", editedEvent.getStatus());
    assertEquals("Physical", editedEvent.getLocation());
  }

  @Test
  public void testHandleEditEventClickedEventNotFound() {
    Map<String, Object> updatedData = new HashMap<>();
    updatedData.put("name", "Nonexistent");
    updatedData.put("startDate", "2024-11-20");
    updatedData.put("endDate", "2024-11-20");
    updatedData.put("startTime", "10:00");
    updatedData.put("endTime", "11:00");
    updatedData.put("location", "Online");
    updatedData.put("status", "Public");
    updatedData.put("description", "Test");

    testView.currentDate = LocalDate.of(2024, 11, 20);
    controller.handleEditEventClicked(updatedData);

    assertTrue(testView.eventExistsErrorShown);
  }

  @Test
  public void testHandleCreateCalendarClicked() {
    controller.handleCreateCalendarClicked();
    assertTrue(testView.createCalendarDialogShown);
  }

  @Test
  public void testHandleCreateCalendarDialogBoxClickedSuccess() {
    controller.handleCreateCalendarDialogBoxClicked("NewCal", "America/Los_Angeles");

    Calendar newCal = calendarManager.getCalendar("NewCal");
    assertNotNull(newCal);
    assertEquals("NewCal", newCal.getCalendarName());
  }

  @Test
  public void testHandleCreateCalendarDialogBoxClickedDuplicateName() {
    controller.handleCreateCalendarDialogBoxClicked("TestCalendar", "America/New_York");

    assertTrue(testView.calendarNameExistsErrorShown);
  }

  @Test
  public void testHandleCalendarClicked() {
    controller.handleCalendarClicked("TestCalendar");
    assertTrue(testView.calendarOptionsMenuShown);
    assertEquals("TestCalendar", testView.calendarOptionsMenuName);
  }

  @Test
  public void testGetCurrentCalendarName() {
    String result = controller.getCurrentCalendarName();
    assertEquals("TestCalendar", result);
  }

  @Test
  public void testGetCurrentCalendarTimezone() {
    String result = controller.getCurrentCalendarTimezone();
    assertEquals("America/New_York", result);
  }

  @Test
  public void testGetCalendarTimezoneExists() {
    String result = controller.getCalendarTimezone("TestCalendar");
    assertEquals("America/New_York", result);
  }

  @Test
  public void testGetCalendarTimezoneDoesNotExist() {
    String result = controller.getCalendarTimezone("Nonexistent");
    assertEquals("Unknown", result);
  }

  @Test
  public void testHandleUseCalendarClicked() {
    Calendar newCal = new CalendarImpl("Personal", java.time.ZoneId.of("America/Chicago"));
    calendarManager.saveCalendar(newCal);

    controller.handleUseCalendarClicked("Personal");

    assertEquals("Personal", calendarManager.getCurrentCalendarName());
  }

  @Test
  public void testHandleEditCalendarNameClickedSuccess() {
    controller.handleEditCalendarNameClicked("TestCalendar", "NewName");

    assertNotNull(calendarManager.getCalendar("NewName"));
    assertNull(calendarManager.getCalendar("TestCalendar"));
  }

  @Test
  public void testHandleEditCalendarNameClickedDuplicateName() {
    Calendar workCal = new CalendarImpl("Work", java.time.ZoneId.of("America/New_York"));
    calendarManager.saveCalendar(workCal);

    controller.handleEditCalendarNameClicked("TestCalendar", "Work");

    assertTrue(testView.calendarNameExistsErrorShown);
  }

  @Test
  public void testHandleEditCalendarTimezoneClicked() {
    controller.handleEditCalendarTimezoneClicked("TestCalendar", "America/Los_Angeles");

    Calendar cal = calendarManager.getCalendar("TestCalendar");
    assertEquals("America/Los_Angeles", cal.getCalendarTimeZone().toString());
  }

  @Test
  public void testHandleSearchEventsFindsResults() {
    Calendar currentCal = calendarManager.getCalendar("TestCalendar");
    LocalDateTime start1 = LocalDateTime.of(2024, 11, 20, 10, 0);
    LocalDateTime end1 = LocalDateTime.of(2024, 11, 20, 11, 0);
    Event event1 = currentCal.createEvent("Meeting", start1, end1, null,
        "Online", "Public", "Test");
    currentCal.saveEvent(event1);

    LocalDateTime start2 = LocalDateTime.of(2024, 11, 21, 14, 0);
    LocalDateTime end2 = LocalDateTime.of(2024, 11, 21, 15, 0);
    Event event2 = currentCal.createEvent("Meeting", start2, end2, null,
        "Online", "Public", "Test2");
    currentCal.saveEvent(event2);

    controller.handleSearchEvents("Meeting");

    assertNotNull(testView.searchResults);
    assertEquals(2, testView.searchResults.size());
    assertEquals("Meeting", testView.searchTerm);
  }

  @Test
  public void testHandleSearchEventsNoResults() {
    controller.handleSearchEvents("Nonexistent");

    assertNotNull(testView.searchResults);
    assertEquals(0, testView.searchResults.size());
  }

  @Test
  public void testHandleSearchEventsCaseSensitivity() {
    Calendar currentCal = calendarManager.getCalendar("TestCalendar");
    LocalDateTime start = LocalDateTime.of(2024, 11, 20, 10, 0);
    LocalDateTime end = LocalDateTime.of(2024, 11, 20, 11, 0);
    Event event = currentCal.createEvent("MEETING", start, end, null,
        "Online", "Public", "Test");
    currentCal.saveEvent(event);

    controller.handleSearchEvents("meeting");

    assertNotNull(testView.searchResults);
    assertEquals(1, testView.searchResults.size());
  }

  @Test
  public void testCompleteWorkflowCreateAndViewEvent() {
    Map<String, Object> params = new HashMap<>();
    params.put("name", "Workshop");
    params.put("startDate", "2024-11-21");
    params.put("endDate", "2024-11-21");
    params.put("startTime", "14:00");
    params.put("endTime", "16:00");
    params.put("location", "Online");
    params.put("status", "Public");
    params.put("description", "Training");
    params.put("isRecurring", false);

    testView.currentDate = LocalDate.of(2024, 11, 21);
    controller.handleDialogBoxCreateEvent(params);
    controller.handleDateClicked();

    assertNotNull(testView.shownEvents);
    assertEquals(1, testView.shownEvents.size());
    assertEquals("Workshop", testView.shownEvents.get(0).getSubject());
  }

  @Test
  public void testMultipleCalendarsWorkflow() {
    Calendar workCal = new CalendarImpl("Work", java.time.ZoneId.of("America/New_York"));
    Calendar personalCal = new CalendarImpl("Personal", java.time.ZoneId.of("America/Chicago"));
    calendarManager.saveCalendar(workCal);
    calendarManager.saveCalendar(personalCal);

    controller.handleNavigateCalendarClicked();
    controller.handleUseCalendarClicked("Personal");

    assertNotNull(testView.shownCalendars);
    assertEquals("Personal", calendarManager.getCurrentCalendarName());
  }

  private static class TestView implements ViewInterface {
    boolean handlersAdded = false;
    boolean createEventDialogShown = false;
    boolean editEventDialogShown = false;
    boolean createCalendarDialogShown = false;
    boolean eventExistsErrorShown = false;
    boolean calendarNameExistsErrorShown = false;
    boolean calendarOptionsMenuShown = false;
    String calendarOptionsMenuName = null;
    List<String> shownCalendars = null;
    List<Event> shownEvents = null;
    List<Event> searchResults = null;
    String searchTerm = null;
    LocalDate currentDate = LocalDate.now();
    Event editEventDialogEvent = null;

    @Override
    public void addHandlers(GuiControllerFeatures handler) {
      handlersAdded = true;
    }

    @Override
    public void showCreateEventDialogBox() {
      createEventDialogShown = true;
    }

    @Override
    public void showEditEventDialogBox(Event event) {
      editEventDialogShown = true;
      editEventDialogEvent = event;
    }

    @Override
    public void showCreateCalendarDialogBox() {
      createCalendarDialogShown = true;
    }

    @Override
    public void showEventExistsError(Event event, boolean isEdit, boolean isDuplicate) {
      eventExistsErrorShown = true;
    }

    @Override
    public void showEditSeriesDialogBox(Event event) {

    }

    @Override
    public void showCalendarNameExistsError(String name, boolean isEdit, String oldName) {
      calendarNameExistsErrorShown = true;
    }

    @Override
    public void showCalendarOptionsMenu(String calendarName, int x, int y) {
      calendarOptionsMenuShown = true;
      calendarOptionsMenuName = calendarName;
    }

    @Override
    public void updateCurrentCalendarLabel(String calendarName, String timezone) {

    }

    @Override
    public void showCalendars(List<String> calendarNames) {
      shownCalendars = new ArrayList<>(calendarNames);
    }

    @Override
    public void showEventsOfTheDay(List<Event> events) {
      shownEvents = new ArrayList<>(events);
    }

    @Override
    public void showSearchResults(List<Event> results, String searchTerm) {
      this.searchResults = new ArrayList<>(results);
      this.searchTerm = searchTerm;
    }

    @Override
    public LocalDate getCurrentDate() {
      return currentDate;
    }
  }
}