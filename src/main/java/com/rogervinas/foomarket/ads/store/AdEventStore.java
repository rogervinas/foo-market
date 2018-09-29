package com.rogervinas.foomarket.ads.store;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import com.rogervinas.foomarket.ads.exceptions.AdNotFoundException;
import java.util.stream.Stream;

public interface AdEventStore {
  int nextId();
  void save(AdBaseEvent event);
  Stream<AdBaseEvent> load(int id) throws AdNotFoundException;
  void subscribe(AdEventConsumer consumer);
}
