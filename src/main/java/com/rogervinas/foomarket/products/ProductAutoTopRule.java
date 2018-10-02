package com.rogervinas.foomarket.products;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import com.rogervinas.foomarket.ads.events.AdProductAddedEvent;
import com.rogervinas.foomarket.ads.store.AdEventStore;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class ProductAutoTopRule {

  private final Logger LOGGER = LoggerFactory.getLogger(ProductAutoTopRule.class);

  private final RestTemplate rest = new RestTemplate();
  private final int port;

  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

  public ProductAutoTopRule(AdEventStore eventStore, int port) {
    eventStore.subscribe(this::consume);
    this.port = port;
  }

  private void consume(AdBaseEvent event) {
    if (event instanceof AdProductAddedEvent) {
      AdProductAddedEvent productAddedEvent = (AdProductAddedEvent) event;
      if (productAddedEvent.getProduct().equalsIgnoreCase("publish")) {
        executor.schedule(() -> addProductTop(event.getId()), 0, TimeUnit.SECONDS);
        executor.schedule(() -> removeProductTop(event.getId()), 10, TimeUnit.SECONDS);
      }
    }
  }

  private void addProductTop(int id) {
    try {
      LOGGER.info("add top " + id);
      rest.exchange(productURL(id), HttpMethod.PUT, productTopRequest(), String.class);
    } catch (Exception e) {
      LOGGER.error("add top " + id, e);
    }
  }

  private void removeProductTop(int id) {
    try {
      LOGGER.info("remove top " + id);
      rest.exchange(productURL(id), HttpMethod.DELETE, productTopRequest(), String.class);
    } catch (Exception e) {
      LOGGER.error("remove top " + id, e);
    }
  }

  private String productURL(int id) {
    return "http://localhost:" + port + "/ad/" + id + "/product";
  }

  private HttpEntity<String> productTopRequest() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>("{\"product\":\"top\"}", headers);
  }
}
