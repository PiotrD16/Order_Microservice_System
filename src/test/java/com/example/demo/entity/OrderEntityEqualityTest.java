package com.example.demo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderEntityEqualityTest {

    @Test
    void transientEntitiesAreOnlyEqualToThemselves() {
        OrderEntity first = order(null, "customer-1");
        OrderEntity second = order(null, "customer-1");

        assertTrue(first.equals(first));
        assertFalse(first.equals(second));
    }

    @Test
    void persistedEntitiesRequireMatchingIdAndCustomerId() {
        UUID id = UUID.randomUUID();
        OrderEntity first = order(id, "customer-1");

        assertEquals(first, order(id, "customer-1"));
        assertNotEquals(first, order(id, "customer-2"));
        assertNotEquals(first, order(UUID.randomUUID(), "customer-1"));
    }

    @Test
    void hashCodeRemainsStableWhenIdAndCustomerIdChange() {
        OrderEntity order = order(null, "customer-1");
        int initialHashCode = order.hashCode();
        HashSet<OrderEntity> orders = new HashSet<>();
        orders.add(order);

        order.setId(UUID.randomUUID());
        order.setCustomerId("customer-2");

        assertEquals(initialHashCode, order.hashCode());
        assertTrue(orders.contains(order));
    }

    private OrderEntity order(UUID id, String customerId) {
        OrderEntity order = new OrderEntity();
        order.setId(id);
        order.setCustomerId(customerId);
        return order;
    }
}
