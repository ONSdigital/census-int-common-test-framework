package uk.gov.ons.ctp.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** MVC Helper */
public class MvcHelper {

  /**
   * Mock Http Servlet Request Builder
   *
   * @param url url to request
   * @return MockHttpServletRequestBuilder Mock Http Servlet Request Builder
   */
  public static MockHttpServletRequestBuilder getJson(String url) {
    return get(url).accept(MediaType.APPLICATION_JSON);
  }

  /**
   * Mock Http Servlet Request Builder
   *
   * @param url url to request
   * @param content json content to post
   * @return MockHttpServletRequestBuilder Mock Http Servlet Request Builder
   */
  public static MockHttpServletRequestBuilder postJson(String url, String content) {
    return post(url)
        .content(content)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);
  }

  /**
   * Mock Http Servlet Request Builder
   *
   * @param url url to request
   * @param content json content
   * @return MockHttpServletRequestBuilder Mock Http Servlet Request Builder
   */
  public static MockHttpServletRequestBuilder putJson(String url, String content) {
    return put(url)
        .content(content)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);
  }

  /**
   * Mock Http Servlet Request Builder
   *
   * @param url url to request
   * @param content xml content
   * @return MockHttpServletRequestBuilder Mock Http Servlet Request Builder
   */
  public static MockHttpServletRequestBuilder postXml(String url, String content) {
    return post(url)
        .content(content)
        .contentType(MediaType.TEXT_XML)
        .accept(MediaType.APPLICATION_JSON);
  }
}
