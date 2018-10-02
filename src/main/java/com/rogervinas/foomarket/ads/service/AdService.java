package com.rogervinas.foomarket.ads.service;

import com.rogervinas.foomarket.ads.entities.Ad;
import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
import com.rogervinas.foomarket.ads.events.AdPriceUpdatedEvent;
import com.rogervinas.foomarket.ads.events.AdProductAddedEvent;
import com.rogervinas.foomarket.ads.events.AdProductRemovedEvent;
import com.rogervinas.foomarket.ads.events.AdRemovedEvent;
import com.rogervinas.foomarket.ads.exceptions.AdNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdService.class);

  private final AdEventStore eventStore;
  private final AdEventStream eventStream;

  public AdService(AdEventStore eventStore, AdEventStream eventStream) {
    this.eventStore = eventStore;
    this.eventStream = eventStream;
  }

  public Ad create(String name, String description, float price) {
    AdCreatedEvent event = new AdCreatedEvent(eventStore.nextId(), name, description, price);
    eventStore.save(event);
    eventStream.publish(event);
    return Ad.Builder.anAd()
        .withId(event.getId())
        .withName(event.getName())
        .withDescription(event.getDescription())
        .withPrice(event.getPrice())
        .build();
  }

  public void remove(Ad ad) {
    AdRemovedEvent event = new AdRemovedEvent(ad.getId());
    eventStore.save(event);
    eventStream.publish(event);
  }

  public Ad updatePrice(Ad ad, float price) {
    AdPriceUpdatedEvent event = new AdPriceUpdatedEvent(ad.getId(), ad.getPrice(), price);
    eventStore.save(event);
    eventStream.publish(event);
    return Ad.Builder.anAd(ad)
        .withPrice(price)
        .build();
  }

  public Ad addProduct(Ad ad, String product) {
    if (!ad.getProducts().contains(product)) {
      AdProductAddedEvent event = new AdProductAddedEvent(ad.getId(), product);
      eventStore.save(event);
      eventStream.publish(event);
      List<String> products = new ArrayList<>(ad.getProducts());
      products.add(product);
      return Ad.Builder.anAd(ad)
          .withProducts(products)
          .build();
    } else {
      return ad;
    }
  }

  public Ad removeProduct(Ad ad, String product) {
    if (ad.getProducts().contains(product)) {
      AdProductRemovedEvent event = new AdProductRemovedEvent(ad.getId(), product);
      eventStore.save(event);
      eventStream.publish(event);
      List<String> products = new ArrayList<>(ad.getProducts());
      products.remove(product);
      return Ad.Builder.anAd(ad)
          .withProducts(products)
          .build();
    } else {
      return ad;
    }
  }

  public Ad get(int id) {
    // wish I could foldLeft :-(
    Ad ad = null;
    Iterator<AdBaseEvent> iterator = eventStore.load(id).iterator();
    while (iterator.hasNext()) {
      ad = apply(ad, iterator.next());
    }
    if (ad == null) {
      throw new AdNotFoundException(id);
    }
    return ad;
  }

  private Ad apply(Ad ad, AdBaseEvent event) {
    if (event instanceof AdCreatedEvent) {
      assert (ad == null);
      return apply((AdCreatedEvent) event);
    } else {
      assert (ad != null);
      if (event instanceof AdPriceUpdatedEvent) {
        return apply(ad, (AdPriceUpdatedEvent) event);
      } else if (event instanceof AdRemovedEvent) {
        return apply(ad, (AdRemovedEvent) event);
      } else if (event instanceof AdProductAddedEvent) {
        return apply(ad, (AdProductAddedEvent) event);
      } else if (event instanceof AdProductRemovedEvent) {
        return apply(ad, (AdProductRemovedEvent) event);
      }
    }
    LOGGER.error("Cannot apply " + event.getClass());
    return ad;
  }

  private Ad apply(AdCreatedEvent event) {
    return Ad.Builder.anAd()
        .withId(event.getId())
        .withName(event.getName())
        .withDescription(event.getDescription())
        .withPrice(event.getPrice())
        .build();
  }

  private Ad apply(Ad ad, AdPriceUpdatedEvent event) {
    return Ad.Builder.anAd(ad)
        .withPrice(event.getNewPrice())
        .build();
  }

  private Ad apply(Ad ad, AdRemovedEvent event) {
    throw new AdNotFoundException(event.getId());
  }

  private Ad apply(Ad ad, AdProductAddedEvent event) {
    List<String> products = new ArrayList<>(ad.getProducts());
    products.add(event.getProduct());
    return Ad.Builder.anAd(ad)
        .withProducts(products)
        .build();
  }

  private Ad apply(Ad ad, AdProductRemovedEvent event) {
    List<String> products = new ArrayList<>(ad.getProducts());
    products.remove(event.getProduct());
    return Ad.Builder.anAd(ad)
        .withProducts(products)
        .build();
  }
}
