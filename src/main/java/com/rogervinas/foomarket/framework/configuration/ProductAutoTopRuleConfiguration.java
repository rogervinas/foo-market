package com.rogervinas.foomarket.framework.configuration;

import com.rogervinas.foomarket.ads.store.AdEventStore;
import com.rogervinas.foomarket.products.ProductAutoTopRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductAutoTopRuleConfiguration {

  @Bean
  public ProductAutoTopRule productTopRule(
      AdEventStore eventStore,
      @Value("${server.port:8080}") int port
  ) {
    return new ProductAutoTopRule(eventStore, port);
  }
}
