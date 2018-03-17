package br.com.programadorjava.crudRepository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import br.com.programadorjava.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

	Collection<OrderItem> findByclientOrder_id (@Param("clientOrder_id") Integer clientOrder_id);
}
