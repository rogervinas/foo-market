package com.rogervinas.foomarket.ads.events;

import java.util.Objects;

public class AdPriceUpdatedEvent extends AdBaseEvent {

  private final float oldPrice;
  private final float newPrice;

  public AdPriceUpdatedEvent(int id, float oldPrice, float newPrice) {
    super(id);
    this.oldPrice = oldPrice;
    this.newPrice = newPrice;
  }

  public float getOldPrice() {
    return oldPrice;
  }

  public float getNewPrice() {
    return newPrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AdPriceUpdatedEvent that = (AdPriceUpdatedEvent) o;
    return Float.compare(that.oldPrice, oldPrice) == 0 &&
        Float.compare(that.newPrice, newPrice) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), oldPrice, newPrice);
  }
}
