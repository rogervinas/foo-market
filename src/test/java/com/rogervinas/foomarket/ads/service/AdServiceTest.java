package com.rogervinas.foomarket.ads.service;

import com.rogervinas.foomarket.ads.entities.Ad;
import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
import com.rogervinas.foomarket.ads.events.AdPriceUpdatedEvent;
import com.rogervinas.foomarket.ads.events.AdProductAddedEvent;
import com.rogervinas.foomarket.ads.events.AdProductRemovedEvent;
import com.rogervinas.foomarket.ads.events.AdRemovedEvent;
import com.rogervinas.foomarket.ads.exceptions.AdNotFoundException;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdServiceTest {

  private static final int ID = 123;
  private static final String NAME = "name1";
  private static final String DESC = "desc1";
  private static final String PRODUCT1 = "product1";
  private static final String PRODUCT2 = "product2";
  private static final String PRODUCT3 = "product3";
  private static final float PRICE_10 = 10.5F;
  private static final float PRICE_100 = 100.5F;

  @Mock
  private AdEventStore eventStore;
  @Mock
  private AdEventStream eventStream;

  private AdService service;

  @Before
  public void before() {
    service = new AdService(eventStore, eventStream);
  }

  @Test
  public void should_create_ad() {
    when(eventStore.nextId()).thenReturn(ID);

    Ad ad = service.create(NAME, DESC, PRICE_10);

    assertThat(ad.getId()).isEqualTo(ID);
    assertThat(ad.getName()).isEqualTo(NAME);
    assertThat(ad.getDescription()).isEqualTo(DESC);
    assertThat(ad.getPrice()).isEqualTo(PRICE_10);

    verify(eventStore).nextId();
    assertEventSavedAndPublished(new AdCreatedEvent(ID, NAME, DESC, PRICE_10));
  }

  @Test
  public void should_update_price() {
    Ad ad = Ad.Builder.anAd()
        .withId(ID)
        .withPrice(PRICE_10).build();

    Ad updatedAd = service.updatePrice(ad, PRICE_100);

    assertThat(updatedAd.getId()).isEqualTo(ID);
    assertThat(updatedAd.getPrice()).isEqualTo(PRICE_100);

    assertEventSavedAndPublished(new AdPriceUpdatedEvent(ID, PRICE_10, PRICE_100));
  }

  @Test
  public void should_add_first_product() {
    Ad ad = Ad.Builder.anAd().withId(ID).build();

    Ad updatedAd = service.addProduct(ad, PRODUCT1);

    assertThat(updatedAd.getProducts()).containsExactly(PRODUCT1);

    assertEventSavedAndPublished(new AdProductAddedEvent(ID, PRODUCT1));
  }

  @Test
  public void should_add_more_products() {
    Ad ad = Ad.Builder.anAd()
        .withId(ID)
        .withProducts(asList(PRODUCT1))
        .build();

    Ad updatedAd = service.addProduct(ad, PRODUCT2);

    assertThat(updatedAd.getProducts()).containsExactlyInAnyOrder(PRODUCT1, PRODUCT2);

    assertEventSavedAndPublished(new AdProductAddedEvent(ID, PRODUCT2));
  }

  @Test
  public void should_not_add_already_added_product() {
    Ad ad = Ad.Builder.anAd()
        .withId(ID)
        .withProducts(asList(PRODUCT1))
        .build();

    Ad updatedAd = service.addProduct(ad, PRODUCT1);

    assertThat(updatedAd.getProducts()).containsExactly(PRODUCT1);

    assertEventNeitherSavedNorPublished();
  }

  @Test
  public void should_remove_product() {
    Ad ad = Ad.Builder.anAd()
        .withId(ID)
        .withProducts(asList(PRODUCT1, PRODUCT2, PRODUCT3))
        .build();

    Ad updatedAd = service.removeProduct(ad, PRODUCT2);

    assertThat(updatedAd.getProducts()).containsExactlyInAnyOrder(PRODUCT1, PRODUCT3);

    assertEventSavedAndPublished(new AdProductRemovedEvent(ID, PRODUCT2));
  }

  @Test
  public void should_not_remove_already_removed_product() {
    Ad ad = Ad.Builder.anAd()
        .withId(ID)
        .withProducts(asList(PRODUCT1, PRODUCT3))
        .build();

    Ad updatedAd = service.removeProduct(ad, PRODUCT2);

    assertThat(updatedAd.getProducts()).containsExactlyInAnyOrder(PRODUCT1, PRODUCT3);

    assertEventNeitherSavedNorPublished();
  }

  @Test
  public void should_remove_ad() {

    Ad ad = Ad.Builder.anAd().withId(ID).build();

    service.remove(ad);

    assertEventSavedAndPublished(new AdRemovedEvent(ID));
  }

  @Test
  public void should_get_created_ad() {
    when(eventStore.load(ID))
        .thenReturn(Stream.of(new AdCreatedEvent(ID, NAME, DESC, PRICE_10)));

    Ad ad = service.get(ID);

    assertThat(ad.getId()).isEqualTo(ID);
    assertThat(ad.getName()).isEqualTo(NAME);
    assertThat(ad.getDescription()).isEqualTo(DESC);
    assertThat(ad.getPrice()).isEqualTo(PRICE_10);
  }

  @Test
  public void should_get_ad_with_updated_price() {
    when(eventStore.load(ID)).thenReturn(Stream.of(
        new AdCreatedEvent(ID, NAME, DESC, PRICE_10),
        new AdPriceUpdatedEvent(ID, PRICE_10, PRICE_100)
    ));

    Ad ad = service.get(ID);

    assertThat(ad.getId()).isEqualTo(ID);
    assertThat(ad.getName()).isEqualTo(NAME);
    assertThat(ad.getDescription()).isEqualTo(DESC);
    assertThat(ad.getPrice()).isEqualTo(PRICE_100);
  }

  @Test
  public void should_throw_exception_when_getting_a_removed_ad() {
    when(eventStore.load(ID)).thenReturn(Stream.of(
        new AdCreatedEvent(ID, NAME, DESC, PRICE_10),
        new AdPriceUpdatedEvent(ID, PRICE_10, PRICE_100),
        new AdRemovedEvent(ID)
    ));

    assertThatThrownBy(() -> service.get(ID))
        .isInstanceOfSatisfying(AdNotFoundException.class, e -> assertThat(e.getId() == ID));
  }

  @Test
  public void should_throw_exception_when_getting_an_unexisting_ad() {
    when(eventStore.load(ID)).thenReturn(Stream.empty());

    assertThatThrownBy(() -> service.get(ID))
        .isInstanceOfSatisfying(AdNotFoundException.class, e -> assertThat(e.getId() == ID));
  }

  private void assertEventSavedAndPublished(AdBaseEvent event) {
    verify(eventStore).save(eq(event));
    verifyNoMoreInteractions(eventStore);
    verify(eventStream).publish(eq(event));
    verifyNoMoreInteractions(eventStore);
  }

  private void assertEventNeitherSavedNorPublished() {
    verifyNoMoreInteractions(eventStore);
    verifyNoMoreInteractions(eventStream);
  }
}
