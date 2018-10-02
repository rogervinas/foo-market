package com.rogervinas.foomarket.framework.configuration;

import com.rogervinas.foomarket.ads.service.AdEventStream;
import com.rogervinas.foomarket.publications.AdPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdPublisherConfiguration {

  @Bean
  public AdPublisher adPublisher(AdEventStream eventStream) {
    return new AdPublisher(eventStream);
  }
}
