package uk.gov.ons.ctp.common.time;

import lombok.extern.slf4j.Slf4j;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A class to combine all utils required on dates, XMLGregorianCalendar, etc.
 */
@Slf4j
public class DateUtil {
  /**
   * To get a XMLGregorianCalendar for now
   * @return a XMLGregorianCalendar for now
   * @throws DatatypeConfigurationException if it can't create a calendar
   */
  public static XMLGregorianCalendar giveMeCalendarForNow() throws DatatypeConfigurationException {
    java.util.GregorianCalendar gregorianCalendar = new java.util.GregorianCalendar();
    gregorianCalendar.setTime(new Date());

    javax.xml.datatype.XMLGregorianCalendar result = null;
    javax.xml.datatype.DatatypeFactory factory = javax.xml.datatype.DatatypeFactory.newInstance();
    result = factory.newXMLGregorianCalendar(
            gregorianCalendar.get(java.util.GregorianCalendar.YEAR),
            gregorianCalendar.get(java.util.GregorianCalendar.MONTH) + 1,
            gregorianCalendar.get(java.util.GregorianCalendar.DAY_OF_MONTH),
            gregorianCalendar.get(java.util.GregorianCalendar.HOUR_OF_DAY),
            gregorianCalendar.get(java.util.GregorianCalendar.MINUTE),
            gregorianCalendar.get(java.util.GregorianCalendar.SECOND),
            gregorianCalendar.get(java.util.GregorianCalendar.MILLISECOND), 0);
    return result;
  }


  /**
   * To transform a string into XMLGregorianCalendar.
   * If it fails building a XMLGregorianCalendar from the string, it will build a XMLGregorianCalendar for now.
   *
   * @param string the string to transform
   * @param format the format used to parse the string
   * @return the XMLGregorianCalendar
   * @throws DatatypeConfigurationException when a XMLGregorianCalendar for now cannot be built
   */
  public static XMLGregorianCalendar stringToXMLGregorianCalendar(String string, String format)
          throws DatatypeConfigurationException {
    XMLGregorianCalendar result = null;
    Date date;
    SimpleDateFormat simpleDateFormat;
    GregorianCalendar gregorianCalendar;

    simpleDateFormat = new SimpleDateFormat(format);
    try {
      date = simpleDateFormat.parse(string);
      gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
      gregorianCalendar.setTime(date);
      result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
    } catch (ParseException e) {
      log.error(String.format("%s - %s", e.getCause(), e.getMessage()));
      result = DateUtil.giveMeCalendarForNow();
    }

    return result;
  }
}