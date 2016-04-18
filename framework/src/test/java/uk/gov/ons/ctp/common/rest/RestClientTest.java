package uk.gov.ons.ctp.common.rest;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestClientTest {

  @Test
  public void testGetResourceOk() {
    RestClient restClient = new RestClient("http", "localhost", "8080");
    RestTemplate restTemplate = restClient.getRestTemplate();

    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(requestTo("http://localhost:8080/hotels/42")).andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{ \"hairColor\" : \"blonde\", \"shoeSize\" : \"8\"}", MediaType.APPLICATION_JSON));

    FakeDTO fakeDTO = restClient.getResource("/hotels/{hotelId}", FakeDTO.class, "42");
    assertTrue(fakeDTO!=null);
    assertTrue(fakeDTO.getHairColor().equals("blonde"));
    assertTrue(fakeDTO.getShoeSize().equals(8));
    mockServer.verify();
  }

  @Test(expected=RestClientException.class)
  public void testGetResourceReallyNotOk() {
    RestClient restClient = new RestClient("http", "localhost", "8080");
    RestTemplate restTemplate = restClient.getRestTemplate();

    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(requestTo("http://localhost:8080/hotels/42")).andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.CONFLICT));

    restClient.getResource("/hotels/{hotelId}", FakeDTO.class, "42");
  }

  @Test(expected=RestClientException.class)
  public void testGetResourceNotFound() {
    RestClient restClient = new RestClient("http", "localhost", "8080");
    RestTemplate restTemplate = restClient.getRestTemplate();

    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(requestTo("http://localhost:8080/hotels/42")).andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.NOT_FOUND).body("{ \"error\" :{  \"code\" : \"123\", \"message\" : \"123\", \"timestamp\" : \"123\"}}"));

    FakeDTO fakeDTO = restClient.getResource("/hotels/{hotelId}", FakeDTO.class, "42");
    assertTrue(fakeDTO==null);
    mockServer.verify();
  }

  @Test
  public void testGetResourcesOk() {
    RestClient restClient = new RestClient("http", "localhost", "8080");
    RestTemplate restTemplate = restClient.getRestTemplate();

    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(requestTo("http://localhost:8080/hotels")).andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("[{ \"hairColor\" : \"blonde\", \"shoeSize\" : \"8\"},{ \"hairColor\" : \"brown\", \"shoeSize\" : \"12\"}]", MediaType.APPLICATION_JSON));

    List<FakeDTO> fakeDTOs = restClient.getResources("/hotels", FakeDTO[].class);
    assertTrue(fakeDTOs!=null);
    assertTrue(fakeDTOs.size() == 2);
    mockServer.verify();
  }

  @Test
  public void testGetResourcesNoContent() {
    RestClient restClient = new RestClient("http", "localhost", "8080");
    RestTemplate restTemplate = restClient.getRestTemplate();

    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(requestTo("http://localhost:8080/hotels")).andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.NO_CONTENT));

    List<FakeDTO> fakeDTOs = restClient.getResources("/hotels", FakeDTO[].class);
    assertTrue(fakeDTOs!=null);
    assertTrue(fakeDTOs.size() == 0);
    mockServer.verify();
  }
  
  @Test(expected=RestClientException.class)
  public void testGetResourcesReallyNotOk() {
    RestClient restClient = new RestClient("http", "localhost", "8080");
    RestTemplate restTemplate = restClient.getRestTemplate();

    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer.expect(requestTo("http://localhost:8080/hotels")).andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.BAD_REQUEST));

    restClient.getResources("/hotels", FakeDTO[].class);
  }
}
