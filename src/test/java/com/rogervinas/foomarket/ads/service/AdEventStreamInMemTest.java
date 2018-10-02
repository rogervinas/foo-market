package com.rogervinas.foomarket.ads.service;

public class AdEventStreamInMemTest extends AdEventStreamTest {

  @Override
  protected AdEventStream buildAdEventStream() {
    return new AdEventStreamInMem();
  }
}
