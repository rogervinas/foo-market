package com.rogervinas.foomarket.framework.controller;

import com.rogervinas.foomarket.ads.entities.Ad;
import com.rogervinas.foomarket.ads.service.AdEventStore;
import com.rogervinas.foomarket.ads.service.AdService;
import com.rogervinas.foomarket.framework.controller.dto.AdCreateRequest;
import com.rogervinas.foomarket.framework.controller.dto.AdProductRequest;
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

  private final AdService service;

  public AdController(AdService service) {
    this.service = service;
  }

  @PostMapping("/ad")
  public AdResponse adCreate(@RequestBody AdCreateRequest adCreate) {
    Ad ad = service.create(adCreate.name, adCreate.description, adCreate.price);
    return AdResponse.from(ad);
  }

  @GetMapping("/ad/{id}")
  public AdResponse adGet(@PathVariable("id") int id) {
    Ad ad = service.get(id);
    return AdResponse.from(ad);
  }

  @DeleteMapping("/ad/{id}")
  public void adRemove(@PathVariable("id") int id) {
    service.remove(service.get(id));
  }

  @PutMapping("/ad/{id}/price")
  public AdResponse adUpdatePrice(@PathVariable("id") int id, @RequestBody AdUpdatePriceRequest adUpdatePrice) {
    Ad ad = service.updatePrice(service.get(id), adUpdatePrice.price);
    return AdResponse.from(ad);
  }

  @PutMapping("/ad/{id}/product")
  public AdResponse adAddProduct(@PathVariable("id") int id, @RequestBody AdProductRequest adProduct) {
    Ad ad = service.addProduct(service.get(id), adProduct.product);
    return AdResponse.from(ad);
  }

  @DeleteMapping("/ad/{id}/product")
  public AdResponse adRemoveProduct(@PathVariable("id") int id, @RequestBody AdProductRequest adProduct) {
    Ad ad = service.removeProduct(service.get(id), adProduct.product);
    return AdResponse.from(ad);
  }
}
