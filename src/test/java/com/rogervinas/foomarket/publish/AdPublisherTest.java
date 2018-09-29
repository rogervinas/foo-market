package com.rogervinas.foomarket.publish;

import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
import com.rogervinas.foomarket.ads.events.AdPriceUpdatedEvent;
import com.rogervinas.foomarket.ads.events.AdRemovedEvent;
import com.rogervinas.foomarket.ads.store.AdEventConsumer;
import com.rogervinas.foomarket.ads.store.AdEventStore;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class AdPublisherTest {

  private AdPublisher publisher;
  private AdEventConsumer consumer;

  @Before
  public void before() {
    AdEventConsumerCaptor consumerCaptor = new AdEventConsumerCaptor();
    AdEventStore eventStore = mock(AdEventStore.class);
    doAnswer(consumerCaptor).when(eventStore).subscribe(any());
    publisher = new AdPublisher(eventStore);
    consumer = consumerCaptor.get();
  }

  @Test
  public void should_return_empty() {
    assertThat(publisher.getAdViews()).isEmpty();
  }

  @Test
  public void should_return_one_adView() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    assertThat(publisher.getAdViews())
        .containsExactly("name1 [desc1] (10.5)");
  }

  @Test
  public void should_return_one_adView_with_updatedPrice() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdPriceUpdatedEvent(100, 10.5F, 20.8F));
    assertThat(publisher.getAdViews())
        .containsExactly("name1 [desc1] (20.8)");
  }

  @Test
  public void should_return_empty_after_remove() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdPriceUpdatedEvent(100, 10.5F, 20.8F));
    consumer.accept(new AdRemovedEvent(100));
    assertThat(publisher.getAdViews()).isEmpty();
  }

  @Test
  public void should_return_many_adViews() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdPriceUpdatedEvent(100, 10.5F, 20.8F));
    consumer.accept(new AdCreatedEvent(200, "name2", "desc2", 20.2F));
    consumer.accept(new AdCreatedEvent(300, "name3", "desc3", 30.6F));
    consumer.accept(new AdRemovedEvent(100));
    consumer.accept(new AdPriceUpdatedEvent(200, 20.2F, 40.4F));
    assertThat(publisher.getAdViews())
        .containsExactly(
            "name2 [desc2] (40.4)",
            "name3 [desc3] (30.6)"
        );
  }

  private static class AdEventConsumerCaptor implements Supplier<AdEventConsumer>, Answer<Void> {

    private AdEventConsumer consumer;

    @Override
    public AdEventConsumer get() {
      return consumer;
    }

    @Override
    public Void answer(InvocationOnMock invocation) {
      consumer = invocation.getArgument(0);
      return null;
    }
  }
}
