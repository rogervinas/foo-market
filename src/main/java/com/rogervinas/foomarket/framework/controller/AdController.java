package com.rogervinas.foomarket.framework.controller;

import com.rogervinas.foomarket.ads.entities.Ad;
import com.rogervinas.foomarket.ads.store.AdEventStore;
import com.rogervinas.foomarket.framework.controller.dto.AdCreateRequest;
import com.rogervinas.foomarket.framework.controller.dto.AdResponse;
import com.rogervinas.foomarket.framework.controller.dto.AdUpdatePriceRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdController {

  private final AdEventStore eventStore;

  public AdController(AdEventStore eventStore) {
    this.eventStore = eventStore;
  }

  @PostMapping("/ad")
  public AdResponse adCreate(@RequestBody AdCreateRequest adCreate) {
    Ad ad = Ad.create(eventStore, adCreate.name, adCreate.description, adCreate.price);
    return AdResponse.from(ad);
  }

  @GetMapping("/ad/{id}")
  public AdResponse adGet(@PathVariable("id") int id) {
    Ad ad = Ad.get(eventStore, id);
    return AdResponse.from(ad);
  }

  @DeleteMapping("/ad/{id}")
  public void adRemove(@PathVariable("id") int id) {
    Ad.get(eventStore, id).remove();
  }

  @PutMapping("/ad/{id}/price")
  public AdResponse adUpdatePrice(@PathVariable("id") int id, @RequestBody AdUpdatePriceRequest adUpdatePrice) {
    Ad ad = Ad.get(eventStore, id).setPrice(adUpdatePrice.price);
    return AdResponse.from(ad);
  }
}
