package com.rogervinas.foomarket.ads.events;

import java.util.Objects;

public abstract class AdBaseEvent {

  private final int id;

  public AdBaseEvent(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AdBaseEvent that = (AdBaseEvent) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
