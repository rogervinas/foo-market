package com.rogervinas.foomarket.ads.store;

public class AdEventStoreInMemTest extends AdEventStoreTest {

  @Override
  protected AdEventStore buildAdEventStore() {
    return new AdEventStoreInMem();
  }
}
