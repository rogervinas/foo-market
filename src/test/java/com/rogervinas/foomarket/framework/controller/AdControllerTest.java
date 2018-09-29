package com.rogervinas.foomarket.framework.controller;

import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
import com.rogervinas.foomarket.ads.events.AdPriceUpdatedEvent;
import com.rogervinas.foomarket.ads.store.AdEventStore;
import java.util.stream.Stream;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AdController.class)
public class AdControllerTest {

  private static final int ID = 100;
  private static final String NAME = "name1";
  private static final String DESC = "desc1";
  private static final float PRICE_10 = 10;
  private static final float PRICE_20 = 20;

  private static final String AD_RESPONSE_10 = "{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0}";
  private static final String AD_RESPONSE_20 = "{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":20.0}";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AdEventStore eventStore;

  @Test
  public void should_create_ad() throws Exception {
    when(eventStore.nextId()).thenReturn(ID);
    AdCreatedEvent createdEvent = new AdCreatedEvent(ID, NAME, DESC, PRICE_10);
    when(eventStore.load(ID)).thenReturn(Stream.of(createdEvent));

    mockMvc.perform(
          post("/ad")
          .contentType("application/json")
          .content("{\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json(AD_RESPONSE_10, true)
        );

    verify(eventStore).save(eq(createdEvent));
  }

  @Test
  public void should_get_ad() throws Exception {
    AdCreatedEvent createdEvent = new AdCreatedEvent(ID, NAME, DESC, PRICE_10);
    when(eventStore.load(ID)).thenReturn(Stream.of(createdEvent));

    mockMvc.perform(get("/ad/" + ID))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json(AD_RESPONSE_10, true)
        );
  }

  @Test
  public void should_update_ad_price() throws Exception {
    AdCreatedEvent createdEvent = new AdCreatedEvent(ID, NAME, DESC, PRICE_10);
    when(eventStore.load(ID)).thenReturn(Stream.of(createdEvent));

    mockMvc.perform(put("/ad/" + ID + "/price")
          .contentType("application/json")
          .content("{\"price\":" + PRICE_20 + "}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json(AD_RESPONSE_20, true)
        );

    verify(eventStore).save(eq(new AdPriceUpdatedEvent(ID, PRICE_10, PRICE_20)));
  }
}
