package com.rogervinas.foomarket.publish;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
import com.rogervinas.foomarket.ads.events.AdPriceUpdatedEvent;
import com.rogervinas.foomarket.ads.events.AdRemovedEvent;
import com.rogervinas.foomarket.ads.store.AdEventStore;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AdPublisher {

  private final List<AdView> adViews = new ArrayList<>();

  public AdPublisher(AdEventStore eventStore) {
    eventStore.subscribe(this::consume);
  }

  public Stream<String> getAdViews() {
    return adViews.stream().map(AdView::render);
  }

  private void consume(AdBaseEvent event) {
    if (event instanceof AdCreatedEvent) {
      createAdView((AdCreatedEvent) event);
    } else if (event instanceof AdRemovedEvent) {
      removeAdView((AdRemovedEvent) event);
    } else if (event instanceof AdPriceUpdatedEvent) {
      updateAdView((AdPriceUpdatedEvent) event);
    }
  }

  private void createAdView(AdCreatedEvent event) {
    adViews.add(new AdView(event.getId(), event.getName(), event.getDescription(), event.getPrice()));
  }

  private void removeAdView(AdRemovedEvent event) {
    adViews.removeIf(adView -> adView.getId() == event.getId());
  }

  private void updateAdView(AdPriceUpdatedEvent event) {
    adViews.replaceAll(adView -> {
      if (adView.getId() == event.getId()) {
        return new AdView(adView.getId(), adView.getName(), adView.getDescription(), event.getNewPrice());
      } else {
        return adView;
      }
    });
  }

  private static class AdView {
    private final int id;
    private final String name;
    private final String description;
    private final float price;

    public AdView(int id, String name, String description, float price) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.price = price;
    }

    private int getId() {
      return id;
    }

    private String getName() {
      return name;
    }

    private String getDescription() {
      return description;
    }

    private float getPrice() {
      return price;
    }

    public String render() {
      return String.format("%s [%s] (%s)", name, description, price);
    }
  }
}
