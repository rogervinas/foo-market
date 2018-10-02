package com.rogervinas.foomarket.ads.service;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import com.rogervinas.foomarket.ads.exceptions.AdNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AdEventStoreInMem implements AdEventStore {

  private int id = 1;

  private final Map<Integer, List<AdBaseEvent>> events = new HashMap<>();

  public synchronized int nextId() {
    return id++;
  }

  public synchronized void save(AdBaseEvent event) {
    events.computeIfAbsent(event.getId(), id -> new ArrayList<>()).add(event);
  }

  @Override
  public synchronized Stream<AdBaseEvent> load(int id) throws AdNotFoundException {
    List<AdBaseEvent> eventsOfId = events.get(id);
    if (eventsOfId == null) {
      throw new AdNotFoundException(id);
    }
    return eventsOfId.stream();
  }
}
