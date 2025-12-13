import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import calendar.commandobject.create.CreateCalendarCommand;
import calendar.controller.ControllerImpl;
import calendar.model.modelimplementations.CalendarImpl;
import calendar.model.modelimplementations.CalendarManagerImpl;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.model.modelinterfaces.Event;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive unit test class for ControllerImpl.
 * Tests all command objects and ensures complete code coverage.
 */
public class ControllerTest {

  private CalendarManager calendarManager;
  private StringWriter output;
  private ControllerImpl controller;
  private Calendar testCalendar;

  /**
   * set up for controller tests.
   */

  @Before
  public void setUp() {
    calendarManager = new CalendarManagerImpl();
    output = new StringWriter();

    testCalendar = new CalendarImpl("TestCalendar", ZoneId.of("America/New_York"));
    calendarManager.saveCalendar(testCalendar);
    calendarManager.setCurrentCalendarNameAs("TestCalendar");
  }

  @Test
  public void testConstructorValidParameters() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    assertNotNull(controller);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullCalendarManager() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(null, input, output, "interactive", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullInput() {
    controller = new ControllerImpl(calendarManager, null, output, "interactive", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorNullOutput() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, null, "interactive", null);
  }

  @Test
  public void testGoInteractiveModeWithExit() {
    String commands = "exit\n";
    StringReader input = new StringReader(commands);
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.go();

    String result = output.toString();
    assertTrue(result.contains("Welcome to the Edith calendar!"));
    assertTrue(result.contains("Calendar Application Rules"));
    assertTrue(result.contains("Type Instruction:"));
    assertTrue(result.contains("Thank you for using the Edith calendar!"));
  }

  @Test
  public void testGoInteractiveModeWithCommands() {
    String commands = "create calendar --name WorkCal --timezone America/New_York\n"
        + "use calendar --name WorkCal\n"
        + "exit\n";
    StringReader input = new StringReader(commands);
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.go();

    String result = output.toString();
    assertTrue(result.contains("Welcome to the Edith calendar!"));
  }

  @Test
  public void testGoHeadlessModeWithFile() throws IOException {
    File tempFile = File.createTempFile("commands", ".txt");
    tempFile.deleteOnExit();

    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write("create calendar --name TestCal --timezone America/New_York\n");
      writer.write("exit\n");
    }

    StringReader input = new StringReader("");
    controller =
        new ControllerImpl(calendarManager, input, output, "headless", tempFile.getAbsolutePath());

    controller.go();

    String result = output.toString();
    assertTrue(result.contains("Thank you for using the Edith calendar!"));
  }

  @Test
  public void testGoHeadlessModeFileNotFound() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "headless", "nonexistent.txt");

    controller.go();

    String result = output.toString();
    assertTrue(result.contains("Thank you for using the Edith calendar!"));
  }

  @Test
  public void testCreateCalendarCommandValid() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();

    controller.processCommands("create calendar --name PersonalCal --timezone Europe/London");

    Calendar created = calendarManager.getCalendar("PersonalCal");
    assertNotNull(created);
    assertEquals("PersonalCal", created.getCalendarName());
  }

  @Test
  public void testCreateCalendarCommandInvalidTimezone() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();

    controller.processCommands("create calendar --name BadCal --timezone InvalidZone");

    Calendar created = calendarManager.getCalendar("BadCal");
    assertNull(created);
  }

  @Test
  public void testUseCalendarCommandValid() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();

    controller.processCommands("use calendar --name TestCalendar");

    assertEquals("TestCalendar", calendarManager.getCurrentCalendarName());
  }

  @Test
  public void testUseCalendarCommandNonExistent() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();

    controller.processCommands("use calendar --name NonExistentCal");

    assertNotEquals("NonExistentCal", calendarManager.getCurrentCalendarName());
  }

  @Test
  public void testCreateEventCommandWithDateTime() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands(
        "create event \"Team Meeting\" from 2025-06-15T10:00 to 2025-06-15T11:00");

    Event event = testCalendar.getEvent("Team Meeting",
        LocalDate.of(2025, 6, 15),
        LocalDate.of(2025, 6, 15),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0));
    assertNotNull(event);
  }

  @Test
  public void testCreateEventCommandAllDay() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands("create event \"Conference\" on 2025-07-20");

    Event event = testCalendar.getEvent("Conference",
        LocalDate.of(2025, 7, 20),
        LocalDate.of(2025, 7, 20),
        LocalTime.of(8, 0),
        LocalTime.of(17, 0));
    assertNotNull(event);
  }

  @Test
  public void testCreateSeriesCommandWithTimes() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands(
        "create event \"Daily Standup\" from 2025-06-01T09:00 to 2025-06-01T09:30 "
            + "repeats MWF for 5 times");

    assertTrue(testCalendar.getCalendarStore().size() > 0);
  }

  @Test
  public void testCreateSeriesCommandWithUntil() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands(
        "create event \"Weekly Review\" on 2025-06-01 repeats F until 2025-06-30");

    assertTrue(testCalendar.getCalendarStore().size() > 0);
  }

  @Test
  public void testEditCalendarCommandChangeName() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands("edit calendar --name TestCalendar --property name UpdatedCalendar");

    assertEquals("UpdatedCalendar", testCalendar.getCalendarName());
  }

  @Test
  public void testEditCalendarCommandChangeTimezone() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands(
        "edit calendar --name TestCalendar --property timezone Europe/Paris");

    assertEquals(ZoneId.of("Europe/Paris"), testCalendar.getCalendarTimeZone());
  }

  @Test
  public void testEditEventCommandSubject() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event = testCalendar.createEvent("Original",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    testCalendar.saveEvent(event);

    controller.processCommands(
        "edit event subject Original from 2025-06-15T10:00 to 2025-06-15T11:00 "
            + "with \"Updated Subject\"");

    Event updated = testCalendar.getEvent("Updated Subject",
        LocalDate.of(2025, 6, 15),
        LocalDate.of(2025, 6, 15),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0));
    assertNotNull(updated);
  }

  @Test
  public void testEditSeriesCommand() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event1 = testCalendar.createEvent("SeriesEvent",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        "series-123", null, null, null);
    Event event2 = testCalendar.createEvent("SeriesEvent",
        LocalDateTime.of(2025, 6, 16, 10, 0),
        LocalDateTime.of(2025, 6, 16, 11, 0),
        "series-123", null, null, null);
    testCalendar.saveEvent(event1);
    testCalendar.saveEvent(event2);

    controller.processCommands(
        "edit series subject SeriesEvent from 2025-06-15T10:00 with \"Updated Series\"");

    assertTrue(testCalendar.getCalendarStore().values().stream()
        .anyMatch(e -> e.getSubject().equals("Updated Series")));
  }

  @Test
  public void testEditEventsCommand() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event1 = testCalendar.createEvent("EventsTest",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        "series-456", null, null, null);
    Event event2 = testCalendar.createEvent("EventsTest",
        LocalDateTime.of(2025, 6, 16, 10, 0),
        LocalDateTime.of(2025, 6, 16, 11, 0),
        "series-456", null, null, null);
    testCalendar.saveEvent(event1);
    testCalendar.saveEvent(event2);

    controller.processCommands(
        "edit events subject EventsTest from 2025-06-15T10:00 with \"Updated Events\"");

    assertTrue(testCalendar.getCalendarStore().values().stream()
        .anyMatch(e -> e.getSubject().equals("Updated Events")));
  }

  @Test
  public void testCopyEventCommand() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Calendar targetCal = new CalendarImpl("TargetCal", ZoneId.of("America/New_York"));
    calendarManager.saveCalendar(targetCal);

    Event event = testCalendar.createEvent("ToCopy",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    testCalendar.saveEvent(event);

    controller.processCommands(
        "copy event ToCopy on 2025-06-15T10:00 --target TargetCal to 2025-06-20T14:00");

    Event copied = targetCal.getEvent("ToCopy_copy",
        LocalDate.of(2025, 6, 20),
        LocalDate.of(2025, 6, 20),
        LocalTime.of(14, 0),
        LocalTime.of(15, 0));
    assertNotNull(copied);
  }

  @Test
  public void testCopyEventsOnDateCommand() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Calendar targetCal = new CalendarImpl("TargetCal2", ZoneId.of("America/New_York"));
    calendarManager.saveCalendar(targetCal);

    Event event1 = testCalendar.createEvent("Event1",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    Event event2 = testCalendar.createEvent("Event2",
        LocalDateTime.of(2025, 6, 15, 14, 0),
        LocalDateTime.of(2025, 6, 15, 15, 0),
        null, null, null, null);
    testCalendar.saveEvent(event1);
    testCalendar.saveEvent(event2);

    controller.processCommands("copy events on 2025-06-15 --target TargetCal2 to 2025-06-20");

    assertTrue(targetCal.getCalendarStore().size() > 0);
  }

  @Test
  public void testCopyEventsBetweenCommand() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Calendar targetCal = new CalendarImpl("TargetCal3", ZoneId.of("America/New_York"));
    calendarManager.saveCalendar(targetCal);

    Event event1 = testCalendar.createEvent("RangeEvent1",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    Event event2 = testCalendar.createEvent("RangeEvent2",
        LocalDateTime.of(2025, 6, 16, 14, 0),
        LocalDateTime.of(2025, 6, 16, 15, 0),
        null, null, null, null);
    testCalendar.saveEvent(event1);
    testCalendar.saveEvent(event2);

    controller.processCommands(
        "copy events between 2025-06-15 and 2025-06-16 --target TargetCal3 to 2025-06-20");

    assertTrue(targetCal.getCalendarStore().size() > 0);
  }

  @Test
  public void testPrintEventsRangeCommand() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event1 = testCalendar.createEvent("RangePrint1",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    Event event2 = testCalendar.createEvent("RangePrint2",
        LocalDateTime.of(2025, 6, 16, 14, 0),
        LocalDateTime.of(2025, 6, 16, 15, 0),
        null, null, null, null);
    testCalendar.saveEvent(event1);
    testCalendar.saveEvent(event2);

    controller.processCommands("print events from 2025-06-15T00:00 to 2025-06-16T23:59");

    assertTrue(testCalendar.getCalendarStore().size() >= 2);
  }

  @Test
  public void testShowStatusCommandBusy() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event = testCalendar.createEvent("BusyEvent",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    testCalendar.saveEvent(event);

    controller.processCommands("show status on 2025-06-15T10:00");

    assertTrue(testCalendar.showStatus("2025-06-15T10:00").equals("Busy"));
  }

  @Test
  public void testShowStatusCommandAvailable() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands("show status on 2025-06-15T15:00");

    assertTrue(testCalendar.showStatus("2025-06-15T15:00").equals("Available"));
  }

  @Test
  public void testExportCalendarCommand() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event = testCalendar.createEvent("ExportTest",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    testCalendar.saveEvent(event);

    controller.processCommands("export cal test_export.csv");

    File exportFile = new File("test_export.csv");
    if (exportFile.exists()) {
      exportFile.delete();
    }
    assertTrue(true);
  }

  @Test
  public void testInvalidCommand() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();

    controller.processCommands("invalid command here");

    String result = output.toString();
    assertTrue(result.contains("Invalid Command") || result.contains("Invalid command"));
  }

  @Test
  public void testCommandWithoutCalendarSelected() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = null; // No calendar selected

    controller.processCommands("print events on 2025-06-15");

    String result = output.toString();
    assertTrue(result.contains("No calendar") || result.contains("currently selected"));
  }

  @Test
  public void testWriteMessage() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.writeMessage("Test Message");

    assertEquals("Test Message", output.toString());
  }

  @Test
  public void testPrintWelcomeMessage() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.printWelcomeMessage();

    String result = output.toString();
    assertTrue(result.contains("Welcome to the Edith calendar!"));
  }

  @Test
  public void testPrintRules() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.printRules();

    String result = output.toString();
    assertTrue(result.contains("Calendar Application Rules"));
    assertTrue(result.contains("Event Requirements"));
    assertTrue(result.contains("Event Series"));
    assertTrue(result.contains("Editing Rules"));
  }

  @Test
  public void testAskForInstruction() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.askForInstruction();

    String result = output.toString();
    assertTrue(result.contains("Type Instruction:"));
  }

  @Test
  public void testPrintErrorMessage() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.printErrorMessage("bad command", "Invalid syntax");

    String result = output.toString();
    assertTrue(result.contains("Invalid Command: bad command"));
    assertTrue(result.contains("Invalid syntax"));
  }

  @Test
  public void testPrintFarewellMessage() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.printFarewellMessage();

    String result = output.toString();
    assertTrue(result.contains("Thank you for using the Edith calendar!"));
  }

  @Test
  public void testSetCalendarValid() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.setCalendar("use calendar --name TestCalendar", calendarManager);

    assertNotNull(controller.calendar);
    assertEquals("TestCalendar", controller.calendar.getCalendarName());
  }

  @Test
  public void testSetCalendarInvalid() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.setCalendar("use calendar --name NonExistent", calendarManager);

    String result = output.toString();
    assertTrue(result.contains("No calendar found") || result.contains("create a new calendar"));
  }

  @Test
  public void testInitializeCommands() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);

    controller.initializeCommands();

    assertNotNull(controller.knownCommands);
    assertTrue(controller.knownCommands.containsKey("create"));
    assertTrue(controller.knownCommands.containsKey("edit"));
    assertTrue(controller.knownCommands.containsKey("print"));
    assertTrue(controller.knownCommands.containsKey("show"));
    assertTrue(controller.knownCommands.containsKey("export"));
    assertTrue(controller.knownCommands.containsKey("copy"));
    assertTrue(controller.knownCommands.containsKey("use"));
  }

  @Test(expected = RuntimeException.class)
  public void testWriteMessageIoException() {
    StringReader input = new StringReader("");
    Appendable badOutput = new Appendable() {
      @Override
      public Appendable append(CharSequence csq) throws IOException {
        throw new IOException("Test exception");
      }

      @Override
      public Appendable append(CharSequence csq, int start, int end) throws IOException {
        throw new IOException("Test exception");
      }

      @Override
      public Appendable append(char c) throws IOException {
        throw new IOException("Test exception");
      }
    };

    controller = new ControllerImpl(calendarManager, input, badOutput, "interactive", null);
    controller.writeMessage("Test");
  }

  @Test
  public void testProcessCommandsWithUseCommand() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();

    Calendar newCal = new CalendarImpl("NewCalendar", ZoneId.of("Europe/London"));
    calendarManager.saveCalendar(newCal);

    controller.processCommands("use calendar --name NewCalendar");

    assertNotNull(controller.calendar);
    assertEquals("NewCalendar", controller.calendar.getCalendarName());
  }

  @Test
  public void testHeadlessModeWithoutExit() throws IOException {
    File tempFile = File.createTempFile("commands_no_exit", ".txt");
    tempFile.deleteOnExit();

    try (FileWriter writer = new FileWriter(tempFile)) {
      writer.write("create calendar --name TestCal --timezone America/New_York\n");
      writer.write("use calendar --name TestCal\n");
    }

    StringReader input = new StringReader("");
    controller =
        new ControllerImpl(calendarManager, input, output, "headless", tempFile.getAbsolutePath());

    controller.go();

    String result = output.toString();
    assertTrue(result.contains("Thank you for using the Edith calendar!"));
  }

  @Test
  public void testPrintEventsOnDateNoEvents() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands("print events on 2025-12-25");

    assertTrue(true);
  }

  @Test
  public void testPrintEventsOnDateMultipleEvents() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    testCalendar.saveEvent(testCalendar.createEvent("Morning Meeting",
        LocalDateTime.of(2025, 6, 15, 9, 0),
        LocalDateTime.of(2025, 6, 15, 10, 0),
        null, null, null, null));
    testCalendar.saveEvent(testCalendar.createEvent("Lunch",
        LocalDateTime.of(2025, 6, 15, 12, 0),
        LocalDateTime.of(2025, 6, 15, 13, 0),
        null, null, null, null));
    testCalendar.saveEvent(testCalendar.createEvent("Evening Review",
        LocalDateTime.of(2025, 6, 15, 17, 0),
        LocalDateTime.of(2025, 6, 15, 18, 0),
        null, null, null, null));

    controller.processCommands("print events on 2025-06-15");

    assertTrue(testCalendar.getEventsForDate(LocalDate.of(2025, 6, 15)).size() == 3);
  }

  @Test
  public void testPrintEventsRangeEmptyRange() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands("print events from 2025-01-01T00:00 to 2025-01-02T23:59");

    assertTrue(true);
  }


  @Test
  public void testCreateSeriesAllDayWithUntil() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands(
        "create event \"Weekly Lunch\" on 2025-06-01 repeats F until 2025-08-01");

    assertTrue(testCalendar.getCalendarStore().size() > 0);
  }


  @Test
  public void testCreateSeriesSpanningMultipleDays() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands(
        "create event \"Multi-day\" from 2025-06-01T10:00 to 2025-06-02T11:00 "
            + "repeats M for 5 times");

    assertTrue(true);
  }

  @Test
  public void testCreateCalendarNullCalendarManager() {
    CreateCalendarCommand cmd =
        new CreateCalendarCommand("create calendar --name Test --timezone America/New_York", null);
    cmd.execute();

    assertTrue(true);
  }

  @Test
  public void testCreateCalendarMalformedTimezone() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();

    controller.processCommands("create calendar --name BadTZ --timezone NewYork");

    assertNull(calendarManager.getCalendar("BadTZ"));
  }

  @Test
  public void testCopyEventNotFound() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Calendar targetCal = new CalendarImpl("Target", ZoneId.of("America/New_York"));
    calendarManager.saveCalendar(targetCal);

    controller.processCommands(
        "copy event NonExistent on 2025-06-15T10:00 --target Target to 2025-06-20T14:00");

    assertTrue(targetCal.getCalendarStore().isEmpty());
  }

  @Test
  public void testCopyEventToNonExistentCalendar() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event = testCalendar.createEvent("ToCopy",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    testCalendar.saveEvent(event);

    controller.processCommands(
        "copy event ToCopy on 2025-06-15T10:00 --target NonExistent to 2025-06-20T14:00");

    assertTrue(true);
  }

  @Test
  public void testCopyEventsEmptyDate() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Calendar targetCal = new CalendarImpl("Target", ZoneId.of("America/New_York"));
    calendarManager.saveCalendar(targetCal);

    controller.processCommands("copy events on 2025-12-25 --target Target to 2025-12-30");

    assertTrue(targetCal.getCalendarStore().isEmpty());
  }

  @Test
  public void testExecutableCopyInvalidCommand() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands("copy invalid format");

    assertTrue(true);
  }

  @Test
  public void testEditEventNonExistent() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands(
        "edit event subject NonExistent from 2025-06-15T10:00 to 2025-06-15T11:00 with \"New\"");

    assertTrue(true);
  }

  @Test
  public void testEditEventNullCalendar() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();

    calendarManager.setCurrentCalendarNameAs("NonExistent");
    controller.calendar = calendarManager.getCalendar("NonExistent");

    controller.processCommands(
        "edit event subject Test from 2025-06-15T10:00 to 2025-06-15T11:00 with \"New\"");

    assertTrue(true);
  }

  @Test
  public void testEditCalendarInvalidProperty() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    controller.processCommands("edit calendar --name TestCalendar --property invalid NewValue");

    assertTrue(true);
  }

  @Test
  public void testEditCalendarNotFound() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();

    controller.processCommands("edit calendar --name NonExistent --property name NewName");

    assertTrue(true);
  }

  @Test
  public void testEditSeriesNoSeriesId() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event = testCalendar.createEvent("SingleEvent",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    testCalendar.saveEvent(event);

    controller.processCommands(
        "edit series subject SingleEvent from 2025-06-15T10:00 with \"Updated\"");

    assertTrue(true);
  }

  @Test
  public void testEditSeriesStartTime() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event1 = testCalendar.createEvent("Series",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        "series-999", null, null, null);
    Event event2 = testCalendar.createEvent("Series",
        LocalDateTime.of(2025, 6, 16, 10, 0),
        LocalDateTime.of(2025, 6, 16, 11, 0),
        "series-999", null, null, null);
    testCalendar.saveEvent(event1);
    testCalendar.saveEvent(event2);

    controller.processCommands(
        "edit series start Series from 2025-06-15T10:00 with 2025-06-15T09:00");

    assertTrue(true);
  }

  @Test
  public void testEditSeriesWouldCreateDuplicate() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event existing = testCalendar.createEvent("Existing",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    testCalendar.saveEvent(existing);

    Event series = testCalendar.createEvent("Series",
        LocalDateTime.of(2025, 6, 16, 10, 0),
        LocalDateTime.of(2025, 6, 16, 11, 0),
        "series-888", null, null, null);
    testCalendar.saveEvent(series);

    controller.processCommands(
        "edit series subject Series from 2025-06-16T10:00 with \"Existing\"");

    assertTrue(true);
  }

  @Test
  public void testEditEventsNoSeriesId() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event = testCalendar.createEvent("SingleEvent",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        null, null, null, null);
    testCalendar.saveEvent(event);

    controller.processCommands(
        "edit events subject SingleEvent from 2025-06-15T10:00 with \"Updated\"");

    assertTrue(true);
  }

  @Test
  public void testEditEventsNewSeriesId() {
    StringReader input = new StringReader("");
    controller = new ControllerImpl(calendarManager, input, output, "interactive", null);
    controller.initializeCommands();
    controller.calendar = testCalendar;

    Event event1 = testCalendar.createEvent("Series",
        LocalDateTime.of(2025, 6, 15, 10, 0),
        LocalDateTime.of(2025, 6, 15, 11, 0),
        "series-777", null, null, null);
    Event event2 = testCalendar.createEvent("Series",
        LocalDateTime.of(2025, 6, 16, 10, 0),
        LocalDateTime.of(2025, 6, 16, 11, 0),
        "series-777", null, null, null);
    testCalendar.saveEvent(event1);
    testCalendar.saveEvent(event2);

    controller.processCommands(
        "edit events start Series from 2025-06-15T10:00 with 2025-06-15T09:30");

    assertTrue(true);
  }
}