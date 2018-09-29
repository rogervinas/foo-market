package com.rogervinas.foomarket.ads.store;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import com.rogervinas.foomarket.ads.exceptions.AdNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public abstract class AdEventStoreTest {

  private static final int ID_1 = 100;
  private static final int ID_2 = 200;
  private static final int ID_3 = 300;

  private AdEventStore eventStore;

  @Before
  public void before() {
    eventStore = buildAdEventStore();
  }

  protected abstract AdEventStore buildAdEventStore();

  @Test
  public void should_give_nextId() {
    int id1 = eventStore.nextId();
    int id2 = eventStore.nextId();
    assertThat(id2).isEqualTo(id1 + 1);
  }

  @Test
  public void should_throw_exception_when_an_ad_has_no_events() {
    assertThatThrownBy(() -> eventStore.load(ID_1))
        .isInstanceOfSatisfying(AdNotFoundException.class, e -> assertThat(e.getId() == ID_1));
  }

  @Test
  public void should_load_when_there_is_an_ad_with_one_event() {
    AdTestEvent event1 = new AdTestEvent(ID_1);
    eventStore.save(event1);

    assertThat(eventStore.load(1)).containsExactly(event1);
  }

  @Test
  public void should_load_when_there_is_an_ad_with_many_events() {
    AdTestEvent event1 = new AdTestEvent(ID_1);
    AdTestEvent event2 = new AdTestEvent(ID_1);
    AdTestEvent event3 = new AdTestEvent(ID_1);
    eventStore.save(event1);
    eventStore.save(event2);
    eventStore.save(event3);

    assertThat(eventStore.load(ID_1))
        .containsExactly(event1, event2, event3);
  }

  @Test
  public void should_load_when_there_are_many_ads_has_many_events() {
    AdTestEvent event11 = new AdTestEvent(ID_1);
    AdTestEvent event21 = new AdTestEvent(ID_2);
    AdTestEvent event22 = new AdTestEvent(ID_2);
    AdTestEvent event31 = new AdTestEvent(ID_3);
    AdTestEvent event32 = new AdTestEvent(ID_3);
    AdTestEvent event33 = new AdTestEvent(ID_3);
    eventStore.save(event11);
    eventStore.save(event21);
    eventStore.save(event22);
    eventStore.save(event31);
    eventStore.save(event32);
    eventStore.save(event33);

    assertThat(eventStore.load(ID_1))
        .containsExactly(event11);
    assertThat(eventStore.load(ID_2))
        .containsExactly(event21, event22);
    assertThat(eventStore.load(ID_3))
        .containsExactly(event31, event32, event33);
  }

  @Test
  public void should_notify_one_event_to_one_consumer() {
    AdEventConsumer consumer1 = mock(AdEventConsumer.class);
    eventStore.subscribe(consumer1);

    AdTestEvent event1 = new AdTestEvent(ID_1);
    eventStore.save(event1);

    verify(consumer1).accept(same(event1));
    verifyNoMoreInteractions(consumer1);
  }

  @Test
  public void should_notify_many_events_to_one_consumer() {
    AdEventConsumer consumer1 = mock(AdEventConsumer.class);
    eventStore.subscribe(consumer1);

    AdTestEvent event1 = new AdTestEvent(ID_1);
    AdTestEvent event2 = new AdTestEvent(ID_2);
    AdTestEvent event3 = new AdTestEvent(ID_3);
    eventStore.save(event1);
    eventStore.save(event2);
    eventStore.save(event3);

    InOrder verify = inOrder(consumer1);
    verify.verify(consumer1).accept(same(event1));
    verify.verify(consumer1).accept(same(event2));
    verify.verify(consumer1).accept(same(event3));
    verify.verifyNoMoreInteractions();
  }

  @Test
  public void should_notify_many_events_to_many_consumers() {
    AdEventConsumer consumer1 = mock(AdEventConsumer.class);
    AdEventConsumer consumer2 = mock(AdEventConsumer.class);
    AdEventConsumer consumer3 = mock(AdEventConsumer.class);
    eventStore.subscribe(consumer1);
    eventStore.subscribe(consumer2);
    eventStore.subscribe(consumer3);

    AdTestEvent event1 = new AdTestEvent(ID_1);
    AdTestEvent event2 = new AdTestEvent(ID_2);
    AdTestEvent event3 = new AdTestEvent(ID_3);
    eventStore.save(event1);
    eventStore.save(event2);
    eventStore.save(event3);

    InOrder verify = inOrder(consumer1, consumer2, consumer3);
    verify.verify(consumer1).accept(same(event1));
    verify.verify(consumer2).accept(same(event1));
    verify.verify(consumer3).accept(same(event1));
    verify.verify(consumer1).accept(same(event2));
    verify.verify(consumer2).accept(same(event2));
    verify.verify(consumer3).accept(same(event2));
    verify.verify(consumer1).accept(same(event3));
    verify.verify(consumer2).accept(same(event3));
    verify.verify(consumer3).accept(same(event3));
    verify.verifyNoMoreInteractions();
  }

  private class AdTestEvent extends AdBaseEvent {
    public AdTestEvent(int id) {
      super(id);
    }
  }
}
