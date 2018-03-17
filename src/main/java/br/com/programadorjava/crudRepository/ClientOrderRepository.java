package br.com.programadorjava.crudRepository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import br.com.programadorjava.entity.ClientOrder;

public interface ClientOrderRepository extends JpaRepository<ClientOrder, Integer> {
	
	List<ClientOrder> findBycustomer_id (@Param("customer_id") Integer id);

}
