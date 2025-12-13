import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import calendar.model.modelimplementations.EventImpl;
import calendar.model.modelinterfaces.Event;
import calendar.model.modelutility.Status;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.Before;
import org.junit.Test;


/**
 * Comprehensive test class for EventImpl covering Assignment 5 requirements.
 * Tests creation, modification, and all properties including enums (Location, Status).
 */
public class EventTest {

  private Event event1;
  private Event event2;
  private Event event3;
  private Event allDayEvent;
  private Event multiDayEvent;
  private Event seriesEvent;

  /**
   * Set up test events covering various scenarios.
   */
  @Before
  public void setUp() {
    event1 = EventImpl.getEventBuilder()
        .setSubject("Meeting")
        .setStartDate(LocalDate.of(2025, 10, 26))
        .setEndDate(LocalDate.of(2025, 10, 26))
        .setStartTime(LocalTime.of(10, 0))
        .setEndTime(LocalTime.of(11, 0))
        .build();

    event2 = EventImpl.getEventBuilder()
        .setSubject("Lunch")
        .setStartDate(LocalDate.of(2025, 10, 26))
        .setEndDate(LocalDate.of(2025, 10, 26))
        .setStartTime(LocalTime.of(12, 0))
        .setEndTime(LocalTime.of(13, 0))
        .build();

    event3 = EventImpl.getEventBuilder()
        .setSubject("Workshop")
        .setStartDate(LocalDate.of(2025, 10, 27))
        .setEndDate(LocalDate.of(2025, 10, 27))
        .setStartTime(LocalTime.of(9, 0))
        .setEndTime(LocalTime.of(17, 0))
        .build();

    allDayEvent = EventImpl.getEventBuilder()
        .setSubject("Conference")
        .setStartDate(LocalDate.of(2025, 11, 1))
        .setEndDate(LocalDate.of(2025, 11, 1))
        .build();

    multiDayEvent = EventImpl.getEventBuilder()
        .setSubject("Training")
        .setStartDate(LocalDate.of(2025, 11, 5))
        .setEndDate(LocalDate.of(2025, 11, 7))
        .setStartTime(LocalTime.of(9, 0))
        .setEndTime(LocalTime.of(17, 0))
        .build();

    seriesEvent = EventImpl.getEventBuilder()
        .setSubject("Daily Standup")
        .setStartDate(LocalDate.of(2025, 11, 10))
        .setEndDate(LocalDate.of(2025, 11, 10))
        .setStartTime(LocalTime.of(9, 0))
        .setEndTime(LocalTime.of(9, 30))
        .setEventSeriesId("series_001")
        .build();
  }

  @Test
  public void testGetSubject() {
    assertEquals("Meeting", event1.getSubject());
    assertEquals("Lunch", event2.getSubject());
    assertEquals("Conference", allDayEvent.getSubject());
  }

  @Test
  public void testGetStartTime() {
    assertEquals(LocalTime.of(10, 0), event1.getStartTime());
    assertEquals(LocalTime.of(12, 0), event2.getStartTime());
    assertEquals(LocalTime.of(8, 0), allDayEvent.getStartTime()); // Default start
  }

  @Test
  public void testGetEndTime() {
    assertEquals(LocalTime.of(11, 0), event1.getEndTime());
    assertEquals(LocalTime.of(13, 0), event2.getEndTime());
    assertEquals(LocalTime.of(17, 0), allDayEvent.getEndTime()); // Default end
  }

  @Test
  public void testGetStartDate() {
    assertEquals(LocalDate.of(2025, 10, 26), event1.getStartDate());
    assertEquals(LocalDate.of(2025, 10, 27), event3.getStartDate());
    assertEquals(LocalDate.of(2025, 11, 5), multiDayEvent.getStartDate());
  }

  @Test
  public void testGetEndDate() {
    assertEquals(LocalDate.of(2025, 10, 26), event1.getEndDate());
    assertEquals(LocalDate.of(2025, 10, 27), event3.getEndDate());
    assertEquals(LocalDate.of(2025, 11, 7), multiDayEvent.getEndDate());
  }

  @Test
  public void testGetEventSeriesId() {
    assertEquals("series_001", seriesEvent.getEventSeriesId());
    assertNull(event1.getEventSeriesId());
  }

  // ========== Modification Tests ==========

  @Test
  public void testModifySubject() {
    Event modified = event1.modifySubject("Team Meeting");
    assertEquals("Team Meeting", modified.getSubject());
    assertSame(event1, modified); // Should return same instance
  }


  @Test
  public void testModifyStartDate() {
    Event modified = event1.modifyStartDate(LocalDate.of(2025, 10, 27));
    assertEquals(LocalDate.of(2025, 10, 27), modified.getStartDate());
    assertSame(event1, modified);
  }

  @Test
  public void testModifyEndDate() {
    Event modified = event1.modifyEndDate(LocalDate.of(2025, 10, 27));
    assertEquals(LocalDate.of(2025, 10, 27), modified.getEndDate());
    assertSame(event1, modified);
  }

  @Test
  public void testModifyStartTime() {
    Event modified = event1.modifyStartTime(LocalTime.of(11, 0));
    assertEquals(LocalTime.of(11, 0), modified.getStartTime());
    assertSame(event1, modified);
  }

  @Test
  public void testModifyEndTime() {
    Event modified = event1.modifyEndTime(LocalTime.of(12, 0));
    assertEquals(LocalTime.of(12, 0), modified.getEndTime());
    assertSame(event1, modified);
  }

  @Test
  public void testModifySeriesId() {
    Event modified = event1.modifySeriesId("new_series_id");
    assertEquals("new_series_id", modified.getEventSeriesId());
    assertSame(event1, modified);
  }


  @Test
  public void testModifyDescription() {
    Event modified = event1.modifyDescription("Important budget meeting");
    assertEquals("Important budget meeting", modified.getDescription());
    assertSame(event1, modified);
  }

  @Test
  public void testModifyLocationPhysical() {
    Event modified = event1.modifyLocation("Physical");
    assertEquals("Physical", modified.getLocation());
    assertSame(event1, modified);
  }




  @Test
  public void testModifyStatusPublic() {
    Event modified = event2.modifyStatus("Public");
    assertEquals("Public", modified.getStatus());
    assertSame(event2, modified);
  }


  @Test
  public void testEventBuilderDefaults() {
    Event defaultEvent = EventImpl.getEventBuilder()
        .setSubject("Test")
        .build();

    assertNotNull(defaultEvent);
    assertEquals("Test", defaultEvent.getSubject());
    assertEquals(LocalTime.of(8, 0), defaultEvent.getStartTime());
    assertEquals(LocalTime.of(17, 0), defaultEvent.getEndTime());
    assertEquals(LocalDate.now(), defaultEvent.getStartDate());
    assertEquals(LocalDate.now(), defaultEvent.getEndDate());
    assertNull(defaultEvent.getEventSeriesId());
  }

  @Test
  public void testToString() {
    String result = event1.toString();
    assertTrue(result.contains("Meeting"));
    assertTrue(result.contains("2025-10-26"));
    assertTrue(result.contains("10:00"));
    assertTrue(result.contains("11:00"));
  }

  @Test
  public void testToStringWithOptionalProperties() {
    event1.modifyDescription("Budget review");
    event1.modifyLocation("Physical");
    event1.modifyStatus("Private");

    String result = event1.toString();
    assertTrue(result.contains("Budget review"));
    assertTrue(result.contains("Physical"));
    assertTrue(result.contains("Private"));
  }

  @Test
  public void testEquals() {
    Event event1Copy = EventImpl.getEventBuilder()
        .setSubject("Meeting")
        .setStartDate(LocalDate.of(2025, 10, 26))
        .setEndDate(LocalDate.of(2025, 10, 26))
        .setStartTime(LocalTime.of(10, 0))
        .setEndTime(LocalTime.of(11, 0))
        .build();

    assertEquals(event1, event1Copy);
    assertEquals(event1, event1);
    assertFalse(event1.equals(null));
    assertFalse(event1.equals("Not an event"));
    assertNotEquals(event1, event2);
  }

  @Test
  public void testEqualsIgnoresOptionalProperties() {
    Event event1WithDesc = EventImpl.getEventBuilder()
        .setSubject("Meeting")
        .setStartDate(LocalDate.of(2025, 10, 26))
        .setEndDate(LocalDate.of(2025, 10, 26))
        .setStartTime(LocalTime.of(10, 0))
        .setEndTime(LocalTime.of(11, 0))
        .build();

    event1WithDesc.modifyDescription("Some description");
    event1WithDesc.modifyLocation("Online");
    event1WithDesc.modifyStatus("Public");

    assertEquals(event1, event1WithDesc);
  }

  @Test
  public void testHashCode() {
    Event event1Copy = EventImpl.getEventBuilder()
        .setSubject("Meeting")
        .setStartDate(LocalDate.of(2025, 10, 26))
        .setEndDate(LocalDate.of(2025, 10, 26))
        .setStartTime(LocalTime.of(10, 0))
        .setEndTime(LocalTime.of(11, 0))
        .build();

    assertEquals(event1.hashCode(), event1Copy.hashCode());
    assertNotEquals(event1.hashCode(), event2.hashCode());
  }

  @Test
  public void testCompareTo() {
    assertTrue(event1.compareTo(event2) < 0);
    assertTrue(event2.compareTo(event1) > 0);
    assertTrue(event1.compareTo(event3) < 0);
    assertEquals(0, event1.compareTo(event1));
  }

  @Test
  public void testCompareToMultiDay() {
    assertTrue(event1.compareTo(multiDayEvent) < 0);
    assertTrue(multiDayEvent.compareTo(event1) > 0);
  }

  @Test
  public void testCompareToSeries() {
    assertTrue(event1.compareTo(seriesEvent) < 0);
    assertTrue(seriesEvent.compareTo(event1) > 0);
  }

  @Test
  public void testEventBuilderChaining() {
    Event chainedEvent = EventImpl.getEventBuilder()
        .setSubject("Chained Event")
        .setStartDate(LocalDate.of(2025, 11, 1))
        .setEndDate(LocalDate.of(2025, 11, 1))
        .setStartTime(LocalTime.of(14, 0))
        .setEndTime(LocalTime.of(15, 0))
        .setEventSeriesId("chain_series")
        .build();

    assertEquals("Chained Event", chainedEvent.getSubject());
    assertEquals(LocalDate.of(2025, 11, 1), chainedEvent.getStartDate());
    assertEquals(LocalTime.of(14, 0), chainedEvent.getStartTime());
    assertEquals("chain_series", chainedEvent.getEventSeriesId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderNullSubject() {
    EventImpl.getEventBuilder()
        .setSubject(null)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderNullStartDate() {
    EventImpl.getEventBuilder()
        .setSubject("Test")
        .setStartDate(null)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderNullStartTime() {
    EventImpl.getEventBuilder()
        .setSubject("Test")
        .setStartTime(null)
        .build();
  }

  @Test
  public void testDuplicateEventDetection() {
    Event duplicate = EventImpl.getEventBuilder()
        .setSubject("Meeting")
        .setStartDate(LocalDate.of(2025, 10, 26))
        .setEndDate(LocalDate.of(2025, 10, 26))
        .setStartTime(LocalTime.of(10, 0))
        .setEndTime(LocalTime.of(11, 0))
        .build();

    assertEquals(event1, duplicate);
    assertEquals(event1.hashCode(), duplicate.hashCode());
  }

  @Test
  public void testPartialOverlapNotEqual() {
    Event overlapping = EventImpl.getEventBuilder()
        .setSubject("Meeting")
        .setStartDate(LocalDate.of(2025, 10, 26))
        .setEndDate(LocalDate.of(2025, 10, 26))
        .setStartTime(LocalTime.of(10, 30))
        .setEndTime(LocalTime.of(11, 30))
        .build();

    assertNotEquals(event1, overlapping);
  }

  @Test
  public void testMultiWordSubject() {
    Event multiWord = EventImpl.getEventBuilder()
        .setSubject("Team Standup Meeting")
        .setStartDate(LocalDate.of(2025, 11, 15))
        .setEndDate(LocalDate.of(2025, 11, 15))
        .setStartTime(LocalTime.of(9, 0))
        .setEndTime(LocalTime.of(9, 30))
        .build();

    assertEquals("Team Standup Meeting", multiWord.getSubject());
  }

  @Test
  public void testSubjectWithSpecialCharacters() {
    Event specialChars = EventImpl.getEventBuilder()
        .setSubject("Q4 Review - Budget & Planning")
        .setStartDate(LocalDate.of(2025, 12, 1))
        .setEndDate(LocalDate.of(2025, 12, 1))
        .setStartTime(LocalTime.of(14, 0))
        .setEndTime(LocalTime.of(16, 0))
        .build();

    assertEquals("Q4 Review - Budget & Planning", specialChars.getSubject());
  }
}