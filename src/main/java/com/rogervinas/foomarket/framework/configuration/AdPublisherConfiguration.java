package com.rogervinas.foomarket.framework.configuration;

import com.rogervinas.foomarket.ads.store.AdEventStore;
import com.rogervinas.foomarket.publish.AdPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdPublisherConfiguration {

  @Bean
  public AdPublisher adPublisher(AdEventStore eventStore) {
    return new AdPublisher(eventStore);
  }
}
