package com.rogervinas.foomarket.framework.controller;

import com.rogervinas.foomarket.publish.AdPublisher;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdPublisherController {

  private final AdPublisher publisher;

  public AdPublisherController(AdPublisher publisher) {
    this.publisher = publisher;
  }

  @GetMapping("/ads")
  public String getAds() {
    return publisher.getAdViews().collect(Collectors.joining("\n"));
  }
}
