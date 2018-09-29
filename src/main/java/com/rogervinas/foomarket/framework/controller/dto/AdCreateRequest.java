package com.rogervinas.foomarket.framework.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AdCreateRequest {

  @JsonProperty("name")
  public final String name;
  @JsonProperty("description")
  public final String description;
  @JsonProperty("price")
  public final float price;

  @JsonCreator
  public AdCreateRequest(
      @JsonProperty("name") String name,
      @JsonProperty("description") String description,
      @JsonProperty("price") float price
  ) {
    this.name = name;
    this.description = description;
    this.price = price;
  }
}
