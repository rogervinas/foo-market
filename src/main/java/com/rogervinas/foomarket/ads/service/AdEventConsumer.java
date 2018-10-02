package com.rogervinas.foomarket.ads.service;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import java.util.function.Consumer;

public interface AdEventConsumer extends Consumer<AdBaseEvent> {
}
