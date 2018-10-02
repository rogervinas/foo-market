package com.rogervinas.foomarket.framework.controller;

import com.rogervinas.foomarket.ads.entities.Ad;
import com.rogervinas.foomarket.ads.service.AdService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
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
  private AdService service;

  private Ad ad100;

  @Before
  public void before() {
    ad100 = Ad.Builder.anAd()
        .withId(100)
        .withName("name1")
        .withDescription("desc1")
        .withPrice(10.0F)
        .build();
    when(service.get(eq(100))).thenReturn(ad100);
  }
  @Test
  public void should_create_ad() throws Exception {
    when(service.create(eq("name1"), eq("desc1"), eq(10.0F))).thenReturn(ad100);

    mockMvc.perform(
          post("/ad")
          .contentType("application/json")
          .content("{\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0,\"products\":[]}", true)
        );
  }

  @Test
  public void should_get_ad() throws Exception {
    mockMvc.perform(get("/ad/100"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0,\"products\":[]}", true)
        );
  }

  @Test
  public void should_delete_ad() throws Exception {
    mockMvc.perform(delete("/ad/100"))
        .andDo(print())
        .andExpect(status().isOk());

    verify(service).remove(eq(ad100));
  }

  @Test
  public void should_update_ad_price() throws Exception {
    when(service.updatePrice(eq(ad100), eq(20.0F)))
        .thenReturn(Ad.Builder.anAd(ad100).withPrice(20.0F).build());

    mockMvc.perform(put("/ad/100/price")
          .contentType("application/json")
          .content("{\"price\":20.0}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":20.0,\"products\":[]}", true)
        );

    verify(service).updatePrice(eq(ad100), eq(20.0F));
  }

  @Test
  public void should_add_ad_product() throws Exception {
    when(service.addProduct(eq(ad100), eq("product1")))
        .thenReturn(Ad.Builder.anAd(ad100).withProducts(asList("product1")).build());

    mockMvc.perform(put("/ad/100/product")
        .contentType("application/json")
        .content("{\"product\":\"product1\"}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0,\"products\":[\"product1\"]}", true)
        );

    verify(service).addProduct(eq(ad100), eq("product1"));
  }

  @Test
  public void should_remove_ad_product() throws Exception {
    when(service.removeProduct(eq(ad100), eq("product2")))
        .thenReturn(Ad.Builder.anAd(ad100).withProducts(asList("product1", "product3")).build());

    mockMvc.perform(delete("/ad/100/product")
        .contentType("application/json")
        .content("{\"product\":\"product2\"}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content()
            .json("{\"id\":100,\"name\":\"name1\",\"description\":\"desc1\",\"price\":10.0,\"products\":[\"product1\",\"product3\"]}", true)
        );

    verify(service).removeProduct(eq(ad100), eq("product2"));
  }
}
