package com.rogervinas.foomarket.ads.store;

import com.rogervinas.foomarket.ads.events.AdBaseEvent;
import java.util.function.Consumer;

public interface AdEventConsumer extends Consumer<AdBaseEvent> {
}
