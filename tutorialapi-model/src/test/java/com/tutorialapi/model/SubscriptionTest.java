package com.tutorialapi.model;

import com.tutorialapi.model.user.Subscription;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionTest {
    @Test
    void testFrom() {
        assertEquals(Optional.of(Subscription.BASIC), Subscription.from("BASIC"));
        assertEquals(Optional.of(Subscription.BASIC), Subscription.from("basic"));
        assertEquals(Optional.empty(), Subscription.from("unknown-subscription"));
    }

}