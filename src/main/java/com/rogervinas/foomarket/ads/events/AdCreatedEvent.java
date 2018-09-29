package com.rogervinas.foomarket.ads.events;

import java.util.Objects;

public class AdCreatedEvent extends AdBaseEvent {

  private final String name;
  private final String description;
  private final float price;

  public AdCreatedEvent(int id, String name, String description, float price) {
    super(id);
    this.name = name;
    this.description = description;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public float getPrice() {
    return price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AdCreatedEvent that = (AdCreatedEvent) o;
    return Float.compare(that.price, price) == 0 &&
        Objects.equals(name, that.name) &&
        Objects.equals(description, that.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), name, description, price);
  }
}
