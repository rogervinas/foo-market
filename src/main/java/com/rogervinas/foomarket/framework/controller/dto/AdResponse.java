package com.rogervinas.foomarket.framework.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rogervinas.foomarket.ads.entities.Ad;

public class AdResponse {

  @JsonProperty("id")
  public final int id;
  @JsonProperty("name")
  public final String name;
  @JsonProperty("description")
  public final String description;
  @JsonProperty("price")
  public final float price;

  public AdResponse(int id, String name, String description, float price) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
  }

  public static AdResponse from(Ad ad) {
    return new AdResponse(ad.getId(), ad.getName(), ad.getDescription(), ad.getPrice());
  }
}
