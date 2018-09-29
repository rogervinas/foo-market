package com.rogervinas.foomarket.publish;

import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
import com.rogervinas.foomarket.ads.events.AdPriceUpdatedEvent;
import com.rogervinas.foomarket.ads.events.AdProductAddedEvent;
import com.rogervinas.foomarket.ads.events.AdProductRemovedEvent;
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
  public void should_return_empty_if_no_events() {
    assertThat(publisher.getPublications()).isEmpty();
  }

  @Test
  public void should_return_empty_if_no_published_ads() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdProductAddedEvent(100, "foo"));
    assertThat(publisher.getPublications()).isEmpty();
  }

  @Test
  public void should_return_published_ads() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdProductAddedEvent(100, "publish"));
    assertThat(publisher.getPublications())
        .containsExactly("name1 [desc1] (10.5)");
  }

  @Test
  public void should_return_published_ads_with_updated_price() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdProductAddedEvent(100, "publish"));
    consumer.accept(new AdPriceUpdatedEvent(100, 10.5F, 20.8F));
    assertThat(publisher.getPublications())
        .containsExactly("name1 [desc1] (20.8)");
  }

  @Test
  public void should_not_return_unpublished_ads() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdProductAddedEvent(100, "publish"));
    consumer.accept(new AdProductRemovedEvent(100, "publish"));
    assertThat(publisher.getPublications()).isEmpty();
  }

  @Test
  public void should_not_return_removed_ads() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdProductAddedEvent(100, "publish"));
    consumer.accept(new AdRemovedEvent(100));
    assertThat(publisher.getPublications()).isEmpty();
  }

  @Test
  public void should_return_published_top_ads() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdProductAddedEvent(100, "publish"));
    consumer.accept(new AdProductAddedEvent(100, "top"));
    assertThat(publisher.getPublications())
        .containsExactly("name1 [desc1] (10.5) *TOP*");
  }

  @Test
  public void should_return_published_former_top_ads() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdProductAddedEvent(100, "publish"));
    consumer.accept(new AdProductAddedEvent(100, "top"));
    consumer.accept(new AdProductRemovedEvent(100, "top"));
    assertThat(publisher.getPublications())
        .containsExactly("name1 [desc1] (10.5)");
  }

  @Test
  public void should_return_many_published_ads_tops_first() {
    consumer.accept(new AdCreatedEvent(100, "name1", "desc1", 10.5F));
    consumer.accept(new AdPriceUpdatedEvent(100, 10.5F, 20.8F));
    consumer.accept(new AdProductAddedEvent(100, "publish"));
    consumer.accept(new AdCreatedEvent(200, "name2", "desc2", 20.2F));
    consumer.accept(new AdCreatedEvent(300, "name3", "desc3", 30.6F));
    consumer.accept(new AdCreatedEvent(400, "name4", "desc4", 40.9F));
    consumer.accept(new AdRemovedEvent(100));
    consumer.accept(new AdPriceUpdatedEvent(200, 20.2F, 40.4F));
    consumer.accept(new AdProductAddedEvent(400, "publish"));
    consumer.accept(new AdProductAddedEvent(400, "top"));
    consumer.accept(new AdProductAddedEvent(200, "publish"));
    consumer.accept(new AdProductAddedEvent(200, "top"));
    consumer.accept(new AdProductAddedEvent(300, "publish"));
    assertThat(publisher.getPublications())
        .containsExactly(
            "name2 [desc2] (40.4) *TOP*",
            "name4 [desc4] (40.9) *TOP*",
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
