package uk.gov.ons.ctp.common.rest;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestClientErrorHandler implements ResponseErrorHandler {

  private ResponseErrorHandler myErrorHandler = new DefaultResponseErrorHandler();

  public boolean hasError(ClientHttpResponse response) throws IOException {
    return RestClient.isError(response.getStatusCode());
  }

  public void handleError(ClientHttpResponse response) throws IOException {
    log.error("RestClient was sent http status code of {}", response.getStatusCode());
  }

}