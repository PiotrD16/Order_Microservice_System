package com.example.demo.entity;

import com.example.demo.enums.Status;
import com.example.demo.enums.FraudStatus;
import com.example.demo.enums.InventoryStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.generator.EventType;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "fraud_status")
    private FraudStatus fraudStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_status")
    private InventoryStatus inventoryStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Getter
    @Setter(AccessLevel.NONE)
    private List<OrderItemsEntity> items = new ArrayList<>();

    @Generated(event = EventType.INSERT)
    @Column(name = "created_at", 
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP", 
            insertable = false, 
            updatable = false)
    private Instant createdAt;

    @Generated(event = EventType.INSERT)    
    @Column(name = "updated_at", 
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP", 
            insertable = false)
    private Instant updatedAt;

    public void addItem(OrderItemsEntity item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItemsEntity item) {
        if (items.remove(item)) {
            item.setOrder(null);
        }
    }

    @Override 
    public String toString() {
        return "OrderEntity{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", totalAmount=" + totalAmount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", fraudStatus=" + fraudStatus +
                ", inventoryStatus=" + inventoryStatus +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || persistentClass(this) != persistentClass(o)) {
            return false;
        }

        OrderEntity that = (OrderEntity) o;
        UUID thisId = getId();
        return thisId != null
                && thisId.equals(that.getId())
                && Objects.equals(getCustomerId(), that.getCustomerId());
    }

    @Override
    public int hashCode() {
        return persistentClass(this).hashCode();
    }

    private static Class<?> persistentClass(Object entity) {
        if (entity instanceof HibernateProxy proxy) {
            return proxy.getHibernateLazyInitializer().getPersistentClass();
        }
        return entity.getClass();
    }
}