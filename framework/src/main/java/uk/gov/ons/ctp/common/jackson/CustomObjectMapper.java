package uk.gov.ons.ctp.common.jackson;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomObjectMapper extends ObjectMapper {

    public static final String DATE_FORMAT_IN_JSON = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public CustomObjectMapper() {
        this.setDateFormat(new SimpleDateFormat(DATE_FORMAT_IN_JSON));
    }
}