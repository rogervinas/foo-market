package com.rogervinas.foomarket.framework.configuration;

import com.rogervinas.foomarket.ads.service.AdEventStream;
import com.rogervinas.foomarket.products.AdProductAutoTopRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdProductAutoTopRuleConfiguration {

  @Bean
  public AdProductAutoTopRule productTopRule(
      AdEventStream eventStream,
      @Value("${server.port:8080}") int port
  ) {
    return new AdProductAutoTopRule(eventStream, port);
  }
}
