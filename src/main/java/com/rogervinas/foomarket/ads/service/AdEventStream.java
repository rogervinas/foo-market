package com.rogervinas.foomarket.ads.service;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;

public interface AdEventStream {
  void publish(AdBaseEvent event);
  void subscribe(AdEventConsumer consumer);
}
