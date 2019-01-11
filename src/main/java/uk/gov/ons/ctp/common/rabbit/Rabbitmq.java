package uk.gov.ons.ctp.common.rabbit;

import lombok.Data;

@Data
public class Rabbitmq {
  private String username;
  private String password;
  private String host;
  private int port;
  private String virtualHost;
  private String cron;
}
