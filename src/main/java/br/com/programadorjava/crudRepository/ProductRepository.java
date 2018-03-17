package br.com.programadorjava.crudRepository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import br.com.programadorjava.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{

	Optional<Product> findByName (@Param("name") String name);
	
	Collection<Product> findByNameContaining(@Param("name") String name);
	
	
}
