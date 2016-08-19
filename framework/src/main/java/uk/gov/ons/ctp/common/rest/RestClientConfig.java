package uk.gov.ons.ctp.common.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestClientConfig { 
  private String scheme = "http";
  private String host = "localhost";
  private String port = "8080";
  private int retryAttempts = 10;
  private int retryPauseMilliSeconds = 5000;
  private int connectTimeoutMilliSeconds = 5000;
  private int readTimeoutMilliSeconds = 5000;
}
