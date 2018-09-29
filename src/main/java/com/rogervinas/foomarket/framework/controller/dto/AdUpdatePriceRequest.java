package com.rogervinas.foomarket.framework.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AdUpdatePriceRequest {

  @JsonProperty("price")
  public final float price;

  @JsonCreator
  public AdUpdatePriceRequest(
      @JsonProperty("price") float price
  ) {
    this.price = price;
  }
}
