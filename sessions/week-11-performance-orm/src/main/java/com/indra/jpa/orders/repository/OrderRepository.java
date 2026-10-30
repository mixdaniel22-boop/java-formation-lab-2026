package com.indra.jpa.orders.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.indra.jpa.orders.model.Order;
import com.indra.jpa.orders.model.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

	// Versión con N+1: items y product se cargan LAZY al recorrer el resultado.
	// TODO (reto): agrega findByStatusWithItems con JOIN FETCH o @EntityGraph.
	List<Order> findByStatus(OrderStatus status);

	// Listado paginado. TODO (reto): debe seguir sin N+1 (ojo con JOIN FETCH + Pageable).
	Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}