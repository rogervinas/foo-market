package com.rogervinas.foomarket.ads.service;

public class AdEventStoreInMemTest extends AdEventStoreTest {

  @Override
  protected AdEventStore buildAdEventStore() {
    return new AdEventStoreInMem();
  }
}
