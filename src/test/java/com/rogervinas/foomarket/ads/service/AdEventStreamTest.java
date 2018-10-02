package com.rogervinas.foomarket.ads.service;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public abstract class AdEventStreamTest {

  private static final int ID_1 = 100;
  private static final int ID_2 = 200;
  private static final int ID_3 = 300;

  private AdEventStream eventStream;

  @Before
  public void before() {
    eventStream = buildAdEventStream();
  }

  protected abstract AdEventStream buildAdEventStream();

  @Test
  public void should_notify_one_event_to_one_consumer() {
    AdEventConsumer consumer1 = mock(AdEventConsumer.class);
    eventStream.subscribe(consumer1);

    AdTestEvent event1 = new AdTestEvent(ID_1);
    eventStream.publish(event1);

    verify(consumer1).accept(same(event1));
    verifyNoMoreInteractions(consumer1);
  }

  @Test
  public void should_notify_many_events_to_one_consumer() {
    AdEventConsumer consumer1 = mock(AdEventConsumer.class);
    eventStream.subscribe(consumer1);

    AdTestEvent event1 = new AdTestEvent(ID_1);
    AdTestEvent event2 = new AdTestEvent(ID_2);
    AdTestEvent event3 = new AdTestEvent(ID_3);
    eventStream.publish(event1);
    eventStream.publish(event2);
    eventStream.publish(event3);

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
    eventStream.subscribe(consumer1);
    eventStream.subscribe(consumer2);
    eventStream.subscribe(consumer3);

    AdTestEvent event1 = new AdTestEvent(ID_1);
    AdTestEvent event2 = new AdTestEvent(ID_2);
    AdTestEvent event3 = new AdTestEvent(ID_3);
    eventStream.publish(event1);
    eventStream.publish(event2);
    eventStream.publish(event3);

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
