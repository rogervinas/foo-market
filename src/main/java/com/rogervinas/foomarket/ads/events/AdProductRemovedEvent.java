package com.rogervinas.foomarket.ads.events;

public class AdProductRemovedEvent extends AdBaseEvent {

  private final String product;

  public AdProductRemovedEvent(int id, String product) {
    super(id);
    this.product = product;
  }

  public String getProduct() {
    return product;
  }
}
