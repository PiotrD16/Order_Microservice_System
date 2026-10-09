package com.example.demo.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:order-items-schema-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@Transactional
class OrderItemsEntitySchemaTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void persistsItemFieldsAndOrderRelationship() {
        OrderEntity order = validOrder();
        OrderItemsEntity item = validItem();
        order.addItem(item);

        entityManager.persist(order);
        entityManager.flush();
        Long itemId = item.getId();
        entityManager.clear();

        OrderItemsEntity savedItem = entityManager.find(OrderItemsEntity.class, itemId);
        assertNotNull(savedItem);
        assertEquals("product-1", savedItem.getProductId());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(new BigDecimal("4.50"), savedItem.getPricePerUnit());
        assertEquals(order.getId(), savedItem.getOrder().getId());
        assertEquals(1, savedItem.getOrder().getItems().size());
    }

    @Test
    void productIdCannotBeNull() {
        OrderEntity order = validOrder();
        OrderItemsEntity item = validItem();
        item.setProductId(null);
        order.addItem(item);

        assertNullIsRejected(order);
    }

    @Test
    void quantityCannotBeNull() {
        OrderEntity order = validOrder();
        OrderItemsEntity item = validItem();
        item.setQuantity(null);
        order.addItem(item);

        assertNullIsRejected(order);
    }

    @Test
    void pricePerUnitCannotBeNull() {
        OrderEntity order = validOrder();
        OrderItemsEntity item = validItem();
        item.setPricePerUnit(null);
        order.addItem(item);

        assertNullIsRejected(order);
    }

    @Test
    void orderCannotBeNull() {
        OrderItemsEntity item = validItem();

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(item);
            entityManager.flush();
        });
    }

    private void assertNullIsRejected(OrderEntity order) {
        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(order);
            entityManager.flush();
        });
    }

    private OrderEntity validOrder() {
        OrderEntity order = new OrderEntity();
        order.setCustomerId("customer-1");
        order.setTotalAmount(new BigDecimal("9.00"));
        order.setStatus(Status.PENDING);
        return order;
    }

    private OrderItemsEntity validItem() {
        OrderItemsEntity item = new OrderItemsEntity();
        item.setProductId("product-1");
        item.setQuantity(2);
        item.setPricePerUnit(new BigDecimal("4.50"));
        return item;
    }
}
