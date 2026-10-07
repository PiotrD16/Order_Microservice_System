package com.example.demo.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:order-schema-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@Transactional
class OrderEntitySchemaTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void nullableColumnsAcceptNullAndDatabaseCreatesTimestamps() {
        OrderEntity order = validOrder();
        order.setCurrency(null);
        order.setFraudStatus(null);
        order.setInventoryStatus(null);
        Instant beforeInsert = Instant.now();

        entityManager.persist(order);
        entityManager.flush();
        Instant afterInsert = Instant.now();
        entityManager.clear();

        OrderEntity savedOrder = entityManager.find(OrderEntity.class, order.getId());
        assertNotNull(savedOrder);
        assertNull(savedOrder.getCurrency());
        assertNull(savedOrder.getFraudStatus());
        assertNull(savedOrder.getInventoryStatus());
        assertTimestampWasGenerated(savedOrder.getCreatedAt(), beforeInsert, afterInsert);
        assertTimestampWasGenerated(savedOrder.getUpdatedAt(), beforeInsert, afterInsert);
    }

    @Test
    void customerIdCannotBeNull() {
        OrderEntity order = validOrder();
        order.setCustomerId(null);

        assertNullIsRejected(order);
    }

    @Test
    void totalAmountCannotBeNull() {
        OrderEntity order = validOrder();
        order.setTotalAmount(null);

        assertNullIsRejected(order);
    }

    @Test
    void statusCannotBeNull() {
        OrderEntity order = validOrder();
        order.setStatus(null);

        assertNullIsRejected(order);
    }

    private void assertNullIsRejected(OrderEntity order) {
        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(order);
            entityManager.flush();
        });
    }

    private void assertTimestampWasGenerated(Instant timestamp, Instant before, Instant after) {
        assertNotNull(timestamp);
        assertFalse(timestamp.isBefore(before.minusSeconds(1)));
        assertFalse(timestamp.isAfter(after.plusSeconds(1)));
    }

    private OrderEntity validOrder() {
        OrderEntity order = new OrderEntity();
        order.setCustomerId("customer-1");
        order.setTotalAmount(new BigDecimal("12.34"));
        order.setStatus(Status.PENDING);
        return order;
    }
}
