package uk.gov.ons.ctp.common.jersey;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;

/**
 * The REST endpoint controller for testing the CTPJerseyTest class
 */
@Path("/hello")
@Produces({ "application/json" })
@Slf4j
public class HelloWorldEndpoint implements CTPEndpoint {

  @GET
  @Path("/{world}")
  public HelloWorldDTO sayHello(@PathParam("world") String world) throws CTPException {
    log.debug("Entering sayHello with {}", world);

    HelloWorldDTO dto = new HelloWorldDTO();
    dto.setHairColour("brown");
    dto.setShoeSize(10);
    return dto;
  } 
  
  @GET
  @Path("/ctpexception/{world}")
  public HelloWorldDTO ctpexception(@PathParam("world") String world) throws CTPException {
    log.debug("Entering ctpexception with {}", world);

    if (world.equals("world")) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "something %s the %s", "hit", "fan");
    }
    return null;
  }

  @GET
  @Path("/list")
  public List<HelloWorldDTO> sayHelloList() throws CTPException {
    log.debug("Entering sayHelloList");

    HelloWorldDTO dto1 = new HelloWorldDTO();
    dto1.setHairColour("brown");
    dto1.setShoeSize(10);
    HelloWorldDTO dto2 = new HelloWorldDTO();
    dto2.setHairColour("brown");
    dto2.setShoeSize(10);
    List<HelloWorldDTO> testList = Arrays.asList(new HelloWorldDTO [] {dto1,dto2});
    return testList;
  } 
}
