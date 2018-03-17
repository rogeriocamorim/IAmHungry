package br.com.programadorjava.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cousine")
public class Cousine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cousine_id")
	private int id;
	
	private String name;
	
	@JsonIgnore
	@OneToMany(mappedBy = "cousine", fetch = FetchType.EAGER)
	private List<Restaurant> restaurants;
		
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Restaurant> getRestaurants() {
		return restaurants;
	}
	public void setRestaurants(List<Restaurant> restaurants) {
		this.restaurants = restaurants;
	}
	public Cousine() {
	}

}
