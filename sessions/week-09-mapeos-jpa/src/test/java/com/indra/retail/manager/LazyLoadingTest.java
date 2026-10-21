package com.indra.retail.manager;

import com.indra.retail.entities.Address;
import com.indra.retail.entities.Customer;
import com.indra.retail.entities.Order;
import com.indra.retail.entities.OrderItem;
import com.indra.retail.entities.Product;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LazyLoadingTest {

    @Autowired
    private EntityManagerRecordManager manager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long orderId;

    @BeforeEach
    void createOrderWithItems() {
        // Todo en una transacción: las entidades quedan managed y el test no
        // depende de qué cascade tenga el mapeo.
        orderId = transactionTemplate.execute(status -> {
            Customer customer = manager.create(new Customer("Carla", UUID.randomUUID() + "@indra.com", new Address("street", "city", "complement")));
            Product product = manager.create(new Product("Laptop", new BigDecimal("1500.00"), 4));
            Order order = new Order(customer);
            order.addItem(new OrderItem(product, 1, product.getPrice()));
            return manager.create(order).getId();
        });
    }

    @Test
    @DisplayName("Acceder a items fuera de la transacción lanza LazyInitializationException")
    void shouldThrowLazyInitializationExceptionOutsideTransaction() {
        // findById abre y cierra su transacción: la Order sale DETACHED
        Order order = manager.findById(Order.class, orderId).orElseThrow();

        assertThrows(LazyInitializationException.class, () -> order.getItems().size());
    }

    @Test
    @DisplayName("Acceder a items dentro de una transacción activa funciona")
    void shouldLoadItemsInsideTransaction() {
        int itemCount = transactionTemplate.execute(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            return order.getItems().size();
        });

        assertEquals(1, itemCount);
    }

    // ───────────────────────── Retos para participantes ─────────────────────────

    @Test
    @DisplayName("Customer de un Order no se carga hasta que se usa (LAZY)")
    void shouldLoadCustomerLazily() {

        String customerName = transactionTemplate.execute(status -> {
            Order managed = manager.findById(Order.class, orderId).orElseThrow();
            return managed.getCustomer().getName();
        });

        assertEquals("Carla", customerName);
    }

    @Test
    @DisplayName("Quitar un ítem de la lista elimina la fila (orphanRemoval)")
    void shouldRemoveOrphanItem() {
        Long itemId = transactionTemplate.execute(status -> {
            Order managed = manager.findById(Order.class, orderId).orElseThrow();
            OrderItem item = managed.getItems().get(0);
            Long id = item.getId();
            managed.getItems().remove(item);
            return id;
        });

        assertTrue(manager.findById(OrderItem.class, itemId).isEmpty());

        int remainingItems = transactionTemplate.execute(status -> {
            Order managed = manager.findById(Order.class, orderId).orElseThrow();
            return managed.getItems().size();
        });
        assertEquals(0, remainingItems);

    }

    @Test
    @DisplayName("No se puede guardar un OrderItem sin Order")
    void shouldRejectOrphanOrderItem() {
        Product product = manager.create(new Product("Laptop", new BigDecimal("1500.00"), 4));

        assertThrows(DataIntegrityViolationException.class,
                () -> manager.create(new OrderItem(product, 1, BigDecimal.ONE)));
    }

    @Test
    @DisplayName("Borrar un OrderItem no borra el Product")
    void shouldNotDeleteProductWhenDeletingItem() {

        orderId = transactionTemplate.execute(status -> {
            Customer customer = manager.create(new Customer("Vanesa", UUID.randomUUID() + "@indra.com", new Address("street", "city", "complement")));
            Product product = manager.create(new Product("PC", new BigDecimal("15000.00"), 4));
            Order order = new Order(customer);
            order.addItem(new OrderItem(product, 1, product.getPrice()));
            return manager.create(order).getId();
        });

        Long itemId = transactionTemplate.execute(status -> {
            Order managed = manager.findById(Order.class, orderId).orElseThrow();
            OrderItem item = managed.getItems().get(0);
            Long id = item.getId();
            managed.getItems().remove(item);
            return id;
        });

        Long prodFound = manager.findAll(Product.class)
                .stream().filter(prod -> prod.getName().equals("PC")).count();

        assertEquals(1, prodFound);
        assertNull(manager.findById(OrderItem.class, itemId).orElse(null));
    }
}
