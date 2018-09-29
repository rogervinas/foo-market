package com.rogervinas.foomarket.framework.configuration;

import com.rogervinas.foomarket.ads.store.AdEventStore;
import com.rogervinas.foomarket.ads.store.AdEventStoreInMem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdEventStoreConfiguration {

  @Bean
  public AdEventStore adEventStore() {
    return new AdEventStoreInMem();
  }
}
