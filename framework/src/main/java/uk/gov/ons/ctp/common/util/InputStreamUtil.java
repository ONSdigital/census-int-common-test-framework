package uk.gov.ons.ctp.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class to collect together some useful InputStream manipulation methods
 */
@Slf4j
public class InputStreamUtil {
  /**
   * Form content String of ContentDocument.
   * @param is InputStream of Document content
   * @return content String
   */
  private static String getStringFromInputStream(InputStream is) {
    BufferedReader br = null;
    String line;
    StringBuilder sb = new StringBuilder();
    try {
      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
      }
    } catch (Exception e) {
      log.error("Exception thrown while converting template stream to string - msg = {}", e.getMessage());
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          log.error("IOException thrown while closing buffered reader used to convert template stream - msg = {}",
                  e.getMessage());
        }
      }
    }

    return sb.toString();
  }
}
