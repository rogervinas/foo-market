package com.rogervinas.foomarket.ads.events;

public class AdProductAddedEvent extends AdBaseEvent {

  private final String product;

  public AdProductAddedEvent(int id, String product) {
    super(id);
    this.product = product;
  }

  public String getProduct() {
    return product;
  }
}
