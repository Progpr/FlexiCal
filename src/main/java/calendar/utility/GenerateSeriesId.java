package calendar.utility;

import java.util.UUID;

/**
 * Generates unique series Id string for events which are part of series.
 * uses java.util.UUID to generate random string, represents a 128-bit value.
 *
 */

public class GenerateSeriesId {

  /**
   * Static method which can be called to generate unique series id string.
   *
   * @return unique id string
   */

  public static String generateSeriesId() {
    return UUID.randomUUID().toString();

  }
}
