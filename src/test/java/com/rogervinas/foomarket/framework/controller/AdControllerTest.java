package com.rogervinas.foomarket.framework.controller;

import com.rogervinas.foomarket.ads.events.AdCreatedEvent;
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

  @Test
  public void should_get_ad() throws Exception {
    AdCreatedEvent createdEvent = new AdCreatedEvent(100, "name1", "desc1", 10);
    when(eventStore.load(100)).thenReturn(Stream.of(createdEvent));

    mockMvc.perform(get("/ad/100"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0}", true)
        );
  }

  @Test
  public void should_create_ad() throws Exception {
    AdCreatedEvent createdEvent = new AdCreatedEvent(200, "name2", "desc2", 20);
    when(eventStore.nextId()).thenReturn(200);
    when(eventStore.load(200)).thenReturn(Stream.of(createdEvent));

    mockMvc.perform(
          post("/ad")
          .contentType("application/json")
          .content("{\"name\":\"name2\",\"description\":\"desc2\",\"price\":20.0}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":200,\"name\":\"name2\",\"description\":\"desc2\",\"price\":20.0}", true)
        );

    verify(eventStore).save(eq(createdEvent));
  }
}
