package com.rogervinas.foomarket.framework.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rogervinas.foomarket.ads.entities.Ad;
import com.rogervinas.foomarket.ads.store.AdEventStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdController {

  private final AdEventStore eventStore;

  public AdController(AdEventStore eventStore) {
    this.eventStore = eventStore;
  }

  @GetMapping("/ad/{id}")
  public AdResponse getAd(@PathVariable("id") int id) {
    Ad ad = Ad.get(eventStore, id);
    return AdResponse.from(ad);
  }

  @PostMapping("/ad")
  public AdResponse createAd(@RequestBody AdCreateRequest adCreate) {
    Ad ad = Ad.create(eventStore, adCreate.name, adCreate.description, adCreate.price);
    return AdResponse.from(ad);
  }

  private static class AdResponse {

    @JsonProperty("id")
    public final int id;
    @JsonProperty("name")
    public final String name;
    @JsonProperty("description")
    public final String description;
    @JsonProperty("price")
    public final float price;

    private AdResponse(int id, String name, String description, float price) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.price = price;
    }

    private static AdResponse from(Ad ad) {
      return new AdResponse(ad.getId(), ad.getName(), ad.getDescription(), ad.getPrice());
    }
  }

  private static class AdCreateRequest {

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
}
