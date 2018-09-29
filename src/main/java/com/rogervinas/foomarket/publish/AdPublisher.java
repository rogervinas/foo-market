package com.rogervinas.foomarket.publish;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
import com.rogervinas.foomarket.ads.events.AdPriceUpdatedEvent;
import com.rogervinas.foomarket.ads.events.AdProductAddedEvent;
import com.rogervinas.foomarket.ads.events.AdProductRemovedEvent;
import com.rogervinas.foomarket.ads.events.AdRemovedEvent;
import com.rogervinas.foomarket.ads.store.AdEventStore;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class AdPublisher {

  private static final String PUBLISH_PRODUCT = "publish";

  private final List<AdView> adViews = new ArrayList<>();

  public AdPublisher(AdEventStore eventStore) {
    eventStore.subscribe(this::consume);
  }

  public Stream<String> getPublications() {
    return adViews.stream()
        .filter(AdView::isVisible)
        .map(AdView::render);
  }

  private void consume(AdBaseEvent event) {
    if (event instanceof AdCreatedEvent) {
      createAdView((AdCreatedEvent) event);
    } else if (event instanceof AdRemovedEvent) {
      removeAdView(event.getId());
    } else if (event instanceof AdPriceUpdatedEvent) {
      updateAdViewPrice(event.getId(), ((AdPriceUpdatedEvent) event).getNewPrice());
    } else if (event instanceof AdProductAddedEvent) {
      updateAdViewVisibility(event.getId(), ((AdProductAddedEvent) event).getProduct().equalsIgnoreCase(PUBLISH_PRODUCT));
    } else if (event instanceof AdProductRemovedEvent) {
      updateAdViewVisibility(event.getId(), !((AdProductRemovedEvent) event).getProduct().equalsIgnoreCase(PUBLISH_PRODUCT));
    }
  }

  private void createAdView(AdCreatedEvent event) {
    adViews.add(new AdView(event.getId(), event.getName(), event.getDescription(), event.getPrice()));
  }

  private void removeAdView(int id) {
    adViews.removeIf(adView -> adView.getId() == id);
  }

  private void updateAdViewPrice(int id, float price) {
    adViews.replaceAll(adView -> {
      if (adView.getId() == id) {
        return new AdView(adView.getId(), adView.getName(), adView.getDescription(), price, adView.isVisible());
      } else {
        return adView;
      }
    });
  }

  private void updateAdViewVisibility(int id, boolean visible) {
    adViews.replaceAll(adView -> {
      if (adView.getId() == id) {
        return new AdView(adView.getId(), adView.getName(), adView.getDescription(), adView.getPrice(), visible);
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
    private final boolean visible;

    public AdView(int id, String name, String description, float price) {
      this(id, name, description, price, false);
    }

    public AdView(int id, String name, String description, float price, boolean visible) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.price = price;
      this.visible = visible;
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

    public boolean isVisible() {
      return visible;
    }
  }
}
