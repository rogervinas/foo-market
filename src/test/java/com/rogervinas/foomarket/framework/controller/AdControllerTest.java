package com.rogervinas.foomarket.framework.controller;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
import com.rogervinas.foomarket.ads.events.AdPriceUpdatedEvent;
import com.rogervinas.foomarket.ads.events.AdProductAddedEvent;
import com.rogervinas.foomarket.ads.events.AdProductRemovedEvent;
import com.rogervinas.foomarket.ads.events.AdRemovedEvent;
import com.rogervinas.foomarket.ads.store.AdEventStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AdController.class)
public class AdControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AdEventStore eventStore;

  private List<AdBaseEvent> events100;

  @Before
  public void before() {
    events100 = new ArrayList<>();
    when(eventStore.load(100)).thenAnswer(a -> events100.stream());
  }

  @Test
  public void should_create_ad() throws Exception {
    when(eventStore.nextId()).thenReturn(100);

    mockMvc.perform(
          post("/ad")
          .contentType("application/json")
          .content("{\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0,\"products\":[]}", true)
        );

    verify(eventStore).save(eq(new AdCreatedEvent(100, "name1", "desc1", 10)));
  }

  @Test
  public void should_get_ad() throws Exception {
    given_ad_100_is_created();

    mockMvc.perform(get("/ad/100"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0,\"products\":[]}", true)
        );
  }

  @Test
  public void should_delete_ad() throws Exception {
    given_ad_100_is_created();

    mockMvc.perform(delete("/ad/100"))
        .andDo(print())
        .andExpect(status().isOk());

    verify(eventStore).save(eq(new AdRemovedEvent(100)));
  }

  @Test
  public void should_update_ad_price() throws Exception {
    given_ad_100_is_created();

    mockMvc.perform(put("/ad/100/price")
          .contentType("application/json")
          .content("{\"price\":20.0}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":20.0,\"products\":[]}", true)
        );

    verify(eventStore).save(eq(new AdPriceUpdatedEvent(100, 10, 20)));
  }

  @Test
  public void should_add_ad_product() throws Exception {
    given_ad_100_is_created();

    mockMvc.perform(put("/ad/100/product")
        .contentType("application/json")
        .content("{\"product\":\"product1\"}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0,\"products\":[\"product1\"]}", true)
        );

    verify(eventStore).save(eq(new AdProductAddedEvent(100, "product1")));
  }

  @Test
  public void should_remove_ad_product() throws Exception {
    given_ad_100_is_created();
    and_ad_100_has_products("product1", "product2", "product3");

    mockMvc.perform(delete("/ad/100/product")
        .contentType("application/json")
        .content("{\"product\":\"product2\"}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0,\"products\":[\"product1\",\"product3\"]}", true)
        );

    verify(eventStore).save(eq(new AdProductRemovedEvent(100, "product2")));
  }

  private void given_ad_100_is_created() {
    events100.add(new AdCreatedEvent(100, "name1", "desc1", 10));
  }

  private void and_ad_100_has_products(String... products) {
    for(String product : products) {
      events100.add(new AdProductAddedEvent(100, product));
    }
  }
}
