package com.rogervinas.foomarket.ads.entities;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
import com.rogervinas.foomarket.ads.events.AdPriceUpdatedEvent;
import com.rogervinas.foomarket.ads.events.AdProductAddedEvent;
import com.rogervinas.foomarket.ads.events.AdProductRemovedEvent;
import com.rogervinas.foomarket.ads.events.AdRemovedEvent;
import com.rogervinas.foomarket.ads.exceptions.AdNotFoundException;
import com.rogervinas.foomarket.ads.store.AdEventStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ad {

  private static final Logger LOGGER = LoggerFactory.getLogger(Ad.class);

  private final AdEventStore eventStore;
  private final int id;

  private String name;
  private String description;
  private float price;
  private final List<String> products = new ArrayList<>();

  private Ad(AdEventStore eventStore, int id) {
    this.eventStore = eventStore;
    this.id = id;
  }

  public static Ad create(AdEventStore eventStore, String name, String description, float price) {
    Ad ad = new Ad(eventStore, eventStore.nextId());
    ad.name = name;
    ad.description = description;
    ad.price = price;
    eventStore.save(new AdCreatedEvent(ad.id, ad.name, ad.description, ad.price));
    return ad;
  }

  public static Ad get(AdEventStore eventStore, int id) throws AdNotFoundException {
    Ad ad = new Ad(eventStore, id);
    eventStore.load(id).forEach(ad::apply);
    return ad;
  }

  public void remove() {
    eventStore.save(new AdRemovedEvent(id));
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Ad setPrice(float price) {
    float oldPrice = this.price;
    this.price = price;
    eventStore.save(new AdPriceUpdatedEvent(id, oldPrice, price));
    return this;
  }

  public float getPrice() {
    return price;
  }

  public Ad addProduct(String product) {
    if (!products.contains(product)) {
      products.add(product);
      eventStore.save(new AdProductAddedEvent(id, product));
    }
    return this;
  }

  public Ad removeProduct(String product) {
    if (products.contains(product)) {
      products.remove(product);
      eventStore.save(new AdProductRemovedEvent(id, product));
    }
    return this;
  }

  public List<String> getProducts() {
    return Collections.unmodifiableList(products);
  }

  private void apply(AdBaseEvent event) throws AdNotFoundException {
    if (event instanceof AdCreatedEvent) {
      apply((AdCreatedEvent) event);
    } else if (event instanceof AdPriceUpdatedEvent) {
      apply((AdPriceUpdatedEvent) event);
    } else if (event instanceof AdRemovedEvent) {
      apply((AdRemovedEvent) event);
    } else if (event instanceof AdProductAddedEvent) {
      apply((AdProductAddedEvent) event);
    } else if (event instanceof AdProductRemovedEvent) {
      apply((AdProductRemovedEvent) event);
    } else {
      LOGGER.error("Cannot apply " + event.getClass());
    }
  }

  private void apply(AdCreatedEvent event) {
    this.name = event.getName();
    this.description = event.getDescription();
    this.price = event.getPrice();
  }

  private void apply(AdPriceUpdatedEvent event) {
    this.price = event.getNewPrice();
  }

  private void apply(AdRemovedEvent event) {
    throw new AdNotFoundException(event.getId());
  }

  private void apply(AdProductAddedEvent event) {
    this.products.add(event.getProduct());
  }

  private void apply(AdProductRemovedEvent event) {
    this.products.remove(event.getProduct());
  }
}
