package uk.gov.ons.ctp.common.error;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The CTP business exception. The fault code given on construction determines
 * the type of business exception, and the user is obliged to provide a
 * meaningful message that the client will see. Or not. This message should not
 * be displayed to end users through the UI, but may be of use to developers and
 * support staff using postman or reading logs.
 */
@JsonSerialize(using = CTPException.OurExceptionSerializer.class)
public class CTPException extends Exception {

  private static final long serialVersionUID = -1569645569528433069L;
  private static final String UNDEFINED_MSG = "Non Specific Error";

  public static enum Fault {
    SYSTEM_ERROR, RESOURCE_NOT_FOUND, RESOURCE_VERSION_CONFLICT, VALIDATION_FAILED, ACCESS_DENIED;
  }

  private Fault fault;
  private long timestamp = System.currentTimeMillis();

  public CTPException(Fault fault) {
    this(fault, UNDEFINED_MSG, (Object[]) null);
  }

  public CTPException(Fault fault, Throwable cause) {
    this(fault, cause, (cause != null) ? cause.getMessage() : "", (Object[]) null);
  }

  public CTPException(Fault fault, String message, Object... args) {
    this(fault, null, message, args);
  }

  public CTPException(Fault fault, Throwable cause, String message, Object... args) {
    super((message != null) ? String.format(message, args) : "", cause);
    this.fault = fault;
  }

  public Fault getFault() {
    return fault;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public static class OurExceptionSerializer extends JsonSerializer<CTPException> {
    @Override
    public void serialize(CTPException value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

      jgen.writeStartObject();
      jgen.writeFieldName("error");
      jgen.writeStartObject();
      jgen.writeStringField("code", value.getFault().name());
      jgen.writeStringField("timestamp", sdf.format(value.getTimestamp()));
      jgen.writeStringField("message", value.getMessage());

      jgen.writeEndObject();
      jgen.writeEndObject();
    }
  }
}
