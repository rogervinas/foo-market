package com.rogervinas.foomarket.ads.service;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import java.util.stream.Stream;

public interface AdEventStore {
  int nextId();
  void save(AdBaseEvent event);
  Stream<AdBaseEvent> load(int id);
}
