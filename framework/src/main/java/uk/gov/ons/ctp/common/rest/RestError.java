package uk.gov.ons.ctp.common.rest;

import lombok.Data;

@Data
public class RestError {
  private Error error;

  @Data
  public class Error {
    private String code;
    private String timestamp;
    private String message;
  }
}
