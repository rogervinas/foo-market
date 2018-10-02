package com.rogervinas.foomarket.ads.service;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import java.util.ArrayList;
import java.util.List;

public class AdEventStreamInMem implements AdEventStream {

  private final List<AdEventConsumer> consumers = new ArrayList<>();

  @Override
  public synchronized void publish(AdBaseEvent event) {
    consumers.forEach(consumer -> consumer.accept(event));
  }

  @Override
  public synchronized void subscribe(AdEventConsumer consumer) {
     consumers.add(consumer);
  }
}
