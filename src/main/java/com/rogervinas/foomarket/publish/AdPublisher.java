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

  private static final String PRODUCT_PUBLISH = "PUBLISH";
  private static final String PRODUCT_TOP = "TOP";

  private final List<AdView> adViews = new ArrayList<>();

  public AdPublisher(AdEventStore eventStore) {
    eventStore.subscribe(this::consume);
  }

  public Stream<String> getPublications() {
    return adViews.stream()
        .filter(AdView::isVisible)
        .sorted()
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
      switch (((AdProductAddedEvent) event).getProduct().toUpperCase()) {
        case PRODUCT_PUBLISH: updateAdViewVisibility(event.getId(), true); break;
        case PRODUCT_TOP: updateAdViewTop(event.getId(), true); break;
      }
    } else if (event instanceof AdProductRemovedEvent) {
      switch (((AdProductRemovedEvent) event).getProduct().toUpperCase()) {
        case PRODUCT_PUBLISH: updateAdViewVisibility(event.getId(), false); break;
        case PRODUCT_TOP: updateAdViewTop(event.getId(), false); break;
      }
    }
  }

  private void createAdView(AdCreatedEvent event) {
    adViews.add(new AdView(event.getId(), event.getName(), event.getDescription(), event.getPrice(), false, false));
  }

  private void removeAdView(int id) {
    adViews.removeIf(adView -> adView.getId() == id);
  }

  private void updateAdViewPrice(int id, float price) {
    adViews.replaceAll(adView -> {
      if (adView.getId() == id) {
        return adView.newWithPrice(price);
      } else {
        return adView;
      }
    });
  }

  private void updateAdViewVisibility(int id, boolean visible) {
    adViews.replaceAll(adView -> {
      if (adView.getId() == id) {
        return adView.newWithVisible(visible);
      } else {
        return adView;
      }
    });
  }

  private void updateAdViewTop(int id, boolean top) {
    adViews.replaceAll(adView -> {
      if (adView.getId() == id) {
        return adView.newWithTop(top);
      } else {
        return adView;
      }
    });
  }

  private static class AdView implements Comparable<AdView> {
    private final int id;
    private final String name;
    private final String description;
    private final float price;
    private final boolean visible;
    private final boolean top;

    public AdView(int id, String name, String description, float price, boolean visible, boolean top) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.price = price;
      this.visible = visible;
      this.top = top;
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
      return String.format("%s [%s] (%s)%s", name, description, price, top ? " *TOP*" : "");
    }

    public boolean isVisible() {
      return visible;
    }

    public boolean isTop() {
      return top;
    }

    public AdView newWithPrice(float price) {
      return new AdView(id, name, description, price, visible, top);
    }

    public AdView newWithVisible(boolean visible) {
      return new AdView(id, name, description, price, visible, top);
    }

    public AdView newWithTop(boolean top) {
      return new AdView(id, name, description, price, visible, top);
    }

    @Override
    public int compareTo(AdView other) {
      if (top == other.isTop()) {
        return Integer.compare(id, other.getId());
      } else {
        return top ? -1 : 1;
      }
    }
  }
}
