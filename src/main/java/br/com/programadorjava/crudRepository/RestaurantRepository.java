package br.com.programadorjava.crudRepository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import br.com.programadorjava.entity.Cousine;
import br.com.programadorjava.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	
	Collection<Restaurant> findBycousine(@Param("cousine") Cousine cousine);
	
	Optional<Restaurant> findByname (@Param("name") String name);
	
	Collection<Restaurant> findByNameContaining(@Param("name") String name);
	
}
