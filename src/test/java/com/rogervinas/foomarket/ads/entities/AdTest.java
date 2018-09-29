package com.rogervinas.foomarket.ads.entities;

import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
import com.rogervinas.foomarket.ads.events.AdPriceUpdatedEvent;
import com.rogervinas.foomarket.ads.events.AdProductAddedEvent;
import com.rogervinas.foomarket.ads.events.AdProductRemovedEvent;
import com.rogervinas.foomarket.ads.events.AdRemovedEvent;
import com.rogervinas.foomarket.ads.exceptions.AdNotFoundException;
import com.rogervinas.foomarket.ads.store.AdEventStore;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdTest {

  private static final int ID_1 = 100;
  private static final int ID_2 = 200;
  private static final int ID_3 = 300;
  private static final int ID_4 = 400;

  @Mock
  private AdEventStore eventStore;

  @Test
  public void should_create_ad() {
    when(eventStore.nextId()).thenReturn(ID_1);

    Ad ad = Ad.create(eventStore, "name1", "desc1", 10);

    assertThat(ad.getId()).isEqualTo(ID_1);
    assertThat(ad.getName()).isEqualTo("name1");
    assertThat(ad.getDescription()).isEqualTo("desc1");
    assertThat(ad.getPrice()).isEqualTo(10);

    InOrder verify = inOrder(eventStore);
    verify.verify(eventStore).save(eq(new AdCreatedEvent(ID_1, "name1", "desc1", 10)));
    verify.verifyNoMoreInteractions();
  }

  @Test
  public void should_create_ad_and_update_price() {
    when(eventStore.nextId()).thenReturn(ID_1);

    Ad ad = Ad.create(eventStore, "name1", "desc1", 10).setPrice(100);

    assertThat(ad.getId()).isEqualTo(ID_1);
    assertThat(ad.getName()).isEqualTo("name1");
    assertThat(ad.getDescription()).isEqualTo("desc1");
    assertThat(ad.getPrice()).isEqualTo(100);

    InOrder verify = inOrder(eventStore);
    verify.verify(eventStore).save(eq(new AdCreatedEvent(ID_1, "name1", "desc1", 10)));
    verify.verify(eventStore).save(eq(new AdPriceUpdatedEvent(ID_1, 10, 100)));
    verify.verifyNoMoreInteractions();
  }

  @Test
  public void should_create_and_remove_ad() {
    when(eventStore.nextId()).thenReturn(ID_1);

    Ad.create(eventStore, "name1", "desc1", 10).remove();

    InOrder verify = inOrder(eventStore);
    verify.verify(eventStore).save(eq(new AdCreatedEvent(ID_1, "name1", "desc1", 10)));
    verify.verify(eventStore).save(eq(new AdRemovedEvent(ID_1)));
    verify.verifyNoMoreInteractions();
  }

  @Test
  public void should_get_ad_with_one_event() {
    when(eventStore.load(ID_2)).thenReturn(Stream.of(new AdCreatedEvent(ID_2, "name2", "desc2", 20)));

    Ad ad = Ad.get(eventStore, ID_2);

    assertThat(ad.getId()).isEqualTo(ID_2);
    assertThat(ad.getName()).isEqualTo("name2");
    assertThat(ad.getDescription()).isEqualTo("desc2");
    assertThat(ad.getPrice()).isEqualTo(20);
  }

  @Test
  public void should_get_ad_with_price_updated() {
    when(eventStore.load(ID_2)).thenReturn(Stream.of(
        new AdCreatedEvent(ID_2, "name2", "desc2", 20),
        new AdPriceUpdatedEvent(ID_2, 20, 200)
    ));

    Ad ad = Ad.get(eventStore, ID_2);

    assertThat(ad.getId()).isEqualTo(ID_2);
    assertThat(ad.getName()).isEqualTo("name2");
    assertThat(ad.getDescription()).isEqualTo("desc2");
    assertThat(ad.getPrice()).isEqualTo(200);
  }

  @Test
  public void should_throw_exception_when_getting_a_removed_ad() {
    when(eventStore.load(ID_3)).thenReturn(Stream.of(
        new AdCreatedEvent(ID_3, "name3", "desc3", 30),
        new AdPriceUpdatedEvent(ID_3, 30, 300),
        new AdRemovedEvent(ID_3)
    ));

    assertThatThrownBy(() -> Ad.get(eventStore, ID_3))
        .isInstanceOfSatisfying(AdNotFoundException.class, e -> assertThat(e.getId() == ID_3));
  }

  @Test
  public void should_add_product_to_ad() {
    when(eventStore.load(ID_4)).thenReturn(Stream.of(new AdCreatedEvent(ID_4, "name4", "desc4", 40)));

    Ad ad = Ad.get(eventStore, ID_4).addProduct("product1");

    assertThat(ad.getProducts()).containsExactly("product1");

    verify(eventStore).save(eq(new AdProductAddedEvent(ID_4, "product1")));
  }

  @Test
  public void should_remove_product_from_ad() {
    when(eventStore.load(ID_4)).thenReturn(Stream.of(
        new AdCreatedEvent(ID_4, "name4", "desc4", 40),
        new AdProductAddedEvent(ID_4, "product1"),
        new AdProductAddedEvent(ID_4, "product2"),
        new AdProductAddedEvent(ID_4, "product3")
    ));

    Ad ad = Ad.get(eventStore, ID_4).removeProduct("product2");

    assertThat(ad.getProducts()).containsExactly("product1", "product3");

    verify(eventStore).save(eq(new AdProductRemovedEvent(ID_4, "product2")));
  }

  @Test
  public void should_get_ad_with_products_added_and_removed() {
    when(eventStore.load(ID_4)).thenReturn(Stream.of(
        new AdCreatedEvent(ID_4, "name4", "desc4", 40),
        new AdProductAddedEvent(ID_4, "product1"),
        new AdProductAddedEvent(ID_4, "product2"),
        new AdProductAddedEvent(ID_4, "product3"),
        new AdProductRemovedEvent(ID_4, "product2")
    ));

    Ad ad = Ad.get(eventStore, ID_4);

    assertThat(ad.getId()).isEqualTo(ID_4);
    assertThat(ad.getName()).isEqualTo("name4");
    assertThat(ad.getDescription()).isEqualTo("desc4");
    assertThat(ad.getPrice()).isEqualTo(40);
    assertThat(ad.getProducts()).containsExactly("product1", "product3");
  }
}
