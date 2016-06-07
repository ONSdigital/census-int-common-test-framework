package uk.gov.ons.ctp.common.time;

import java.sql.Timestamp;

/**
 * Centralzed DateTime handling for CTP
 *
 */
public class DateTimeUtil {

  /**
   * Looks like overkill I know - but this ensures that we consistently stamp
   * model objects with UTC datetime
   *
   * @return The current time in UTC
   */
  public static Timestamp nowUTC() {
    return new Timestamp(System.currentTimeMillis());
  }

}
