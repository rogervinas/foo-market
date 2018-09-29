package com.rogervinas.foomarket.framework.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AdProductRequest {

  @JsonProperty("product")
  public final String product;

  @JsonCreator
  public AdProductRequest(
      @JsonProperty("product") String product
  ) {
    this.product = product;
  }
}
