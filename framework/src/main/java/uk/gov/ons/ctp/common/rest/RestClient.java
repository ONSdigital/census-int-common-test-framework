package uk.gov.ons.ctp.common.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * A convenience class that wraps the Spring RestTemplate and eases its use
 * around the typing, headers, path and query params
 */
@Slf4j
public class RestClient {

  private String scheme;
  private String host;
  private String port;
  private RestTemplate restTemplate;
  @Inject
  private ObjectMapper objectMapper;

  public static boolean isError(HttpStatus status) {
    HttpStatus.Series series = status.series();
    return (HttpStatus.Series.CLIENT_ERROR.equals(series)
        || HttpStatus.Series.SERVER_ERROR.equals(series));
  }

  /**
   * Construct with the core details of the server
   * 
   * @param scheme http or https
   * @param host hostname of the server
   * @param port the port the service will be running on
   */
  public RestClient(String scheme, String host, String port) {
    super();
    this.scheme = scheme;
    this.host = host;
    this.port = port;
    restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(new RestClientErrorHandler());
    objectMapper = new ObjectMapper();
  }

  public RestTemplate getRestTemplate() {
    return this.restTemplate;
  }

  /**
   * Use to perform a GET that retrieves a single resource
   * 
   * @param path the API path - can contain path params place holders in "{}" ie
   *          "/cases/{caseid}"
   * @param clazz the class type of the resource to be obtained
   * @param pathParams vargs list of params to substitute in the path - note
   *          simply used in order
   * @return the type you asked for! or null
   * @throws RestClientException
   */
  public <T> T getResource(
      String path,
      Class<T> clazz,
      Object... pathParams)
      throws RestClientException {
    return getResource(path, clazz, null, null, pathParams);
  }

  /**
   * Use to perform a GET that retrieves a single resource
   * 
   * @param path the API path - can contain path params place holders in "{}" ie
   *          "/cases/{caseid}"
   * @param clazz the class type of the resource to be obtained
   * @param headerParams map of header of params to be used - can be null
   * @param queryParams multi map of query params keyed by string logically
   *          allows for K:"haircolor",V:"blond" AND K:"shoesize", V:"9","10"
   * @param pathParams vargs list of params to substitute in the path - note
   *          simply used in order
   * @return the type you asked for! or null
   * @throws RestClientException
   */
  public <T> T getResource(
      String path,
      Class<T> clazz,
      Map<String, String> headerParams,
      MultiValueMap<String, String> queryParams,
      Object... pathParams)
      throws RestClientException {

    log.debug("Enter getResources for path : {}", path);

    HttpEntity<?> httpEntity = createHttpEntity(null, headerParams);
    UriComponents uriComponents = createUriComponents(path, queryParams, pathParams);
    ResponseEntity<String> response = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity,
        String.class);
    String responseBody = response.getBody();
    T responseObject = null;
    try {
      if (isError(response.getStatusCode())) {
        if (responseBody != null) {
          RestError error = objectMapper.readValue(responseBody, RestError.class);
          throw new RestClientException(response.getStatusCode() + " [" + error.getError().getCode() + "] " + error.getError().getMessage());
        } else {
          throw new RestClientException(response.getStatusCode().toString());
        }
      } else {
        responseObject = objectMapper.readValue(responseBody, clazz);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return responseObject;
  }

  /**
   * Use to perform a GET that retrieves multiple instances of a resource
   * 
   * @param path the API path - can contain path params place holders in "{}" ie
   *          "/cases/{caseid}"
   * @param clazz the class type of the resource, a List<> of which is to be
   *          obtained
   * @param pathParams vargs list of params to substitute in the path - note
   *          simply used in order
   * @return a list of the type you asked for
   * @throws RestClientException
   */
  public <T> List<T> getResources(
      String path,
      Class<T[]> clazz,
      Object... pathParams)
      throws RestClientException {
    return getResources(path, clazz, null, null, pathParams);
  }

  /**
   * Use to perform a GET that retrieves multiple instances of a resource
   * 
   * @param path the API path - can contain path params place holders in "{}" ie
   *          "/cases/{caseid}"
   * @param clazz the array class type of the resource, a List<> of which is to
   *          be obtained
   * @param headerParams map of header of params to be used - can be null
   * @param queryParams multi map of query params keyed by string logically
   *          allows for K:"haircolor",V:"blond" AND K:"shoesize", V:"9","10"
   * @param pathParams vargs list of params to substitute in the path - note
   *          simply used in order
   * @return a list of the type you asked for
   * @throws RestClientException
   */
  public <T> List<T> getResources(
      String path,
      Class<T[]> clazz,
      Map<String, String> headerParams,
      MultiValueMap<String, String> queryParams,
      Object... pathParams)
      throws RestClientException {

    log.debug("Enter getResources for path : {}", path);

    HttpEntity<?> httpEntity = createHttpEntity(null, headerParams);
    UriComponents uriComponents = createUriComponents(path, queryParams, pathParams);

    ResponseEntity<T[]> response = restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity, clazz);
    if (!response.getStatusCode().equals(HttpStatus.OK) && !response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
      log.error("Failed to get on calling {}", uriComponents.toUri());
      throw new RestClientException("Expected status 200/204 but got " + response.getStatusCode().value());
    }
    List<T> responseList = new ArrayList<T>();
    T[] responseArray = response.getBody();
    if (responseArray != null && responseArray.length > 0) {
      responseList = Arrays.asList(response.getBody());
    }
    return responseList;
  }

  public <T, O> T postResource(
      String path,
      O objToPost,
      Class<T> clazz,
      Object... pathParams)
      throws RestClientException {
    return postResource(path, objToPost, clazz, null, null, pathParams);
  }

  public <T, O> T postResource(
      String path,
      O objToPost,
      Class<T> clazz,
      Map<String, String> headerParams,
      MultiValueMap<String, String> queryParams,
      Object... pathParams)
      throws RestClientException {
    return executePutOrPost(HttpMethod.POST, path, objToPost, clazz, headerParams, queryParams, pathParams);
  }

  public <T, O> T putResource(
      String path,
      O objToPut,
      Class<T> clazz,
      Object... pathParams)
      throws RestClientException {
    return putResource(path, objToPut, clazz, null, null, pathParams);
  }

  public <T, O> T putResource(
      String path,
      O objToPut,
      Class<T> clazz,
      Map<String, String> headerParams,
      MultiValueMap<String, String> queryParams,
      Object... pathParams)
      throws RestClientException {
    return executePutOrPost(HttpMethod.PUT, path, objToPut, clazz, headerParams, queryParams, pathParams);
  }

  private <T, O> T executePutOrPost(
      HttpMethod method,
      String path,
      O objToPut,
      Class<T> clazz,
      Map<String, String> headerParams,
      MultiValueMap<String, String> queryParams,
      Object... pathParams)
      throws RestClientException {
    log.debug("Enter getResources for path : {}", path);

    HttpEntity<O> httpEntity = createHttpEntity(objToPut, headerParams);
    UriComponents uriComponents = createUriComponents(path, queryParams, pathParams);

    ResponseEntity<T> response = restTemplate.exchange(uriComponents.toUri(), method, httpEntity, clazz);
    if (!response.getStatusCode().equals(HttpStatus.OK)) {
      log.error("Failed to put/post on calling {}", uriComponents.toUri());
      throw new RestClientException("Expected status 200 but got " + response.getStatusCode().value());
    }
    return response.getBody();
  }

  /**
   * used to create the URiComponents needed to call an endpoint
   * 
   * @param path the API path - can contain path params place holders in "{}" ie
   *          "/cases/{caseid}"
   * @param queryParams multi map of query params keyed by string logically
   *          allows for K:"haircolor",V:"blond" AND K:"shoesize", V:"9","10"
   * @param pathParams vargs list of params to substitute in the path - note
   *          simply used in order
   * @return the components
   */
  private UriComponents createUriComponents(String path,
      MultiValueMap<String, String> queryParams,
      Object... pathParams) {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme(this.scheme)
        .host(this.host)
        .port(this.port)
        .path(path)
        .queryParams(queryParams)
        .buildAndExpand(pathParams)
        .encode();
    return uriComponents;
  }

  /**
   * used to create the HttpEntity for headers
   * 
   * @param headerParams map of header of params to be used - can be null
   * @return the header entity
   */
  private <H> HttpEntity<H> createHttpEntity(H entity, Map<String, String> headerParams) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
    if (headerParams != null) {
      for (Map.Entry<String, String> me : headerParams.entrySet()) {
        headers.set(me.getKey(), me.getValue());
      }
    }
    HttpEntity<H> httpEntity = new HttpEntity<H>(entity, headers);
    return httpEntity;
  }
}
