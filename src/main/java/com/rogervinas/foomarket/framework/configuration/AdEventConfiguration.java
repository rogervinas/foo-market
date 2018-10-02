package com.rogervinas.foomarket.framework.configuration;

import com.rogervinas.foomarket.ads.service.AdEventStore;
import com.rogervinas.foomarket.ads.service.AdEventStoreInMem;
import com.rogervinas.foomarket.ads.service.AdEventStream;
import com.rogervinas.foomarket.ads.service.AdEventStreamInMem;
import com.rogervinas.foomarket.ads.service.AdService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdEventConfiguration {

  @Bean
  public AdService adService(AdEventStore eventStore, AdEventStream eventStream) {
    return new AdService(eventStore, eventStream);
  }

  @Bean
  public AdEventStore adEventStore() {
    return new AdEventStoreInMem();
  }

  @Bean
  public AdEventStream adEventStream() {
    return new AdEventStreamInMem();
  }
}
