package br.com.programadorjava.restapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import br.com.programadorjava.crudRepository.ClientOrderRepository;
import br.com.programadorjava.crudRepository.CousineRepository;
import br.com.programadorjava.crudRepository.CustomerRepository;
import br.com.programadorjava.crudRepository.OrderItemRepository;
import br.com.programadorjava.crudRepository.ProductRepository;
import br.com.programadorjava.crudRepository.RestaurantRepository;
import br.com.programadorjava.entity.ClientOrder;
import br.com.programadorjava.entity.Cousine;
import br.com.programadorjava.entity.Customer;
import br.com.programadorjava.entity.OrderItem;
import br.com.programadorjava.entity.Product;
import br.com.programadorjava.entity.Restaurant;
import br.com.programadorjava.serializer.CustomClientOrderSerializer;

@RestController
public class Hungry {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	final private String fieldNull = "\"error\": \"Null Fields";
	final private String blankField = "\"error\": \"Blank Fields";
	final private String accWithEmail = "\"error\": \"There is already an account with this email!";
	final private String cousineindb = "\"error\": \"There is already a cousine with this name!";
	final private String restaurantindb = "\"error\": \"There is already a restaurant with this name!";
	final private String productindb = "\"error\": \"There is already a product with this name!";
	final private String cousinnamenotfound = "\"error\": \"The cousin name not found!";
	final private String restaurantenamenotfound = "\"error\": \"The restaurant name not found!";
	final private String noProducts = "\"error\": \"There is no Products to add";

	final private String noDeliveryAddress = "\"error\": \"No delivery adress set.";

	final private String noEmailOrPassword = "\"error\": \"No email or password set.";
	final private String noEmailfound = "\"error\": \"No email found..";

	final private String noUserFound = "\"error\": \"Fail to login user - password do not match";

	final private String productNotIndb = "\"error\": \"There is no product in the system!";

	final private String sucess = "Success";
	final private String noCousine = "No cousine found with the name: ";

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private ClientOrderRepository clientOrderRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CousineRepository cousineRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	// Customer
	@PostMapping("/api/v1/Customer")
	@ResponseBody
	public ResponseEntity<String> customerAdd(@RequestBody Customer customer) {
		if (customer.getAddress() == null || customer.getEmail() == null || customer.getName() == null
				|| customer.getPassword() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fieldNull);
		}
		if (customer.getAddress().isEmpty() || customer.getEmail().isEmpty() || customer.getName().isEmpty()
				|| customer.getPassword().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(blankField);
		}
		Optional<Customer> optcustomer = customerRepository.findByEmail(customer.getEmail());
		if (optcustomer.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(accWithEmail);
		} else {
			customer.setCreation(String.valueOf(new Date()));
			customerRepository.save(customer);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(sucess);
		}
	}

	// COUSINE INIT
	@PostMapping("/api/v1/Cousine/add")
	@ResponseBody
	public ResponseEntity<String> cousineAdd(@RequestBody Cousine cousine) {
		if (cousine.getName() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fieldNull);
		} else {
			if (cousine.getName().isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(blankField);
			} else {
				Optional<Cousine> optCousine = cousineRepository.findByName(cousine.getName());
				if (optCousine.isPresent()) {
					if (optCousine.get().getName() == cousine.getName()) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cousineindb);
					} else {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cousineindb);
					}
				} else {
					cousineRepository.save(cousine);
					return ResponseEntity.status(HttpStatus.ACCEPTED).body(sucess);
				}
			}
		}
	}

	@GetMapping("/api/v1/Cousine")
	@ResponseBody
	public List<Cousine> getAllCousine() {
		return (List<Cousine>) cousineRepository.findAll();
	}

	@GetMapping("/api/v1/Cousine/search/{searchText}")
	@ResponseBody
	public ResponseEntity<Collection<Cousine>> getCousineContainName(@PathVariable("searchText") String searchText) {

		Collection<Cousine> cousines = cousineRepository.findByNameContaining(searchText);
		if (cousines.isEmpty()) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ArrayList<Cousine>());
		} else {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(cousines);

		}
	}

	@GetMapping("/api/v1/Cousine/{cousineId}/stores")
	@ResponseBody
	public ResponseEntity<Collection<Restaurant>> getRestaurantByCousineID(
			@PathVariable("cousineId") Integer cousineId) {
		if (cousineId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		Optional<Cousine> cousine = cousineRepository.findById(cousineId);
		if (!cousine.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
		}
		Collection<Restaurant> restaurant = restaurantRepository.findBycousine(cousine.get());
		if (restaurant.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(restaurant);
	}

	// RESTAURANT INIT
	@PostMapping("/api/v1/Restaurant/add")
	@ResponseBody
	public ResponseEntity<String> restaurantAdd(@RequestBody Restaurant restaurant) {
		if (restaurant.getName() == null || restaurant.getAddress() == null || restaurant.getCousine() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fieldNull);
		}
		if (restaurant.getName().isEmpty() || restaurant.getAddress().isEmpty()
				|| restaurant.getCousine().getName().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(blankField);
		}
		Optional<Restaurant> optRestaurant = restaurantRepository.findByname(restaurant.getName());
		if (optRestaurant.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restaurantindb);
		}
		Optional<Cousine> optCousine = cousineRepository.findByName(restaurant.getCousine().getName());
		if (!optCousine.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cousinnamenotfound);
		}
		restaurant.setCousine(optCousine.get());
		restaurantRepository.save(restaurant);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(sucess);
	}

	@GetMapping("/api/v1/Store")
	@ResponseBody
	public ResponseEntity<Collection<Restaurant>> getAllrestaurants() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body((Collection<Restaurant>) restaurantRepository.findAll());
	}

	@GetMapping("/api/v1/Store/search/{searchText}")
	@ResponseBody
	public ResponseEntity<Collection<Restaurant>> getRestaurantContainName(
			@PathVariable("searchText") String searchText) {
		if (!searchText.isEmpty()) {
			Collection<Restaurant> restaurants = restaurantRepository.findByNameContaining(searchText);
			if (restaurants.isEmpty()) {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ArrayList<Restaurant>());
			} else {
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(restaurants);
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
		}
	}

	@GetMapping("/api/v1/Store/{storeId}")
	@ResponseBody
	public ResponseEntity<Restaurant> getRestaurantByID(@PathVariable("storeId") Integer storeId) {
		if (storeId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		Optional<Restaurant> optRestaurante = restaurantRepository.findById(storeId);
		if (!optRestaurante.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Restaurant());
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(optRestaurante.get());
	}

	// Product
	@PostMapping("/api/v1/Product/add")
	@ResponseBody
	public ResponseEntity<String> productAdd(@RequestBody Product product) {
		if (product.getName() == null || product.getDescription() == null || product.getRestaurant() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(fieldNull);
		}
		if (product.getName().isEmpty() || product.getDescription().isEmpty()
				|| product.getRestaurant().getName().isEmpty() || product.getPrice() == 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(blankField);
		}
		Optional<Product> optProduct = productRepository.findByName(product.getName());
		if (optProduct.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(productindb);
		}
		Optional<Restaurant> optRestaurant = restaurantRepository.findByname(product.getRestaurant().getName());
		if (!optRestaurant.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restaurantenamenotfound);
		}
		product.setRestaurant(optRestaurant.get());
		productRepository.save(product);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(sucess);

	}

	@GetMapping("/api/v1/Product")
	@ResponseBody
	public ResponseEntity<Collection<Product>> getAllProduct() {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body((Collection<Product>) productRepository.findAll());
	}

	@GetMapping("/api/v1/Product/{productId}")
	@ResponseBody
	public ResponseEntity<Product> getProductByID(@PathVariable("productId") Integer productId) {
		if (productId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		Optional<Product> optproduct = productRepository.findById(productId);
		if (!optproduct.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Product());
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(optproduct.get());

	}

	@GetMapping("/api/v1/Product/search/{searchText}")
	@ResponseBody
	public ResponseEntity<Collection<Product>> getProductContainName(@PathVariable("searchText") String searchText) {

		Collection<Product> products = productRepository.findByNameContaining(searchText);
		if (products.isEmpty()) {
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ArrayList<Product>());
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(products);

	}

	// Order
	@PostMapping("/api/v1/Order/add")
	@ResponseBody
	public ResponseEntity<String> addProductToOrder(@RequestBody ClientOrder clientOrder) {
		if (clientOrder.getDeliveryAddress() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noDeliveryAddress);
		}
		if (clientOrder.getCustomer().getEmail().isEmpty() || clientOrder.getCustomer().getPassword().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noEmailOrPassword);
		}
		Optional<Customer> optCustomer = customerRepository.findByEmail(clientOrder.getCustomer().getEmail());

		if (optCustomer.isPresent()) {
			if (!optCustomer.get().getEmail().equals(clientOrder.getCustomer().getEmail())) {
				if (optCustomer.get().getPassword().equals(clientOrder.getCustomer().getPassword())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noEmailOrPassword);
				}
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noEmailfound);
		}
		if (clientOrder.getDeliveryAddress().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noDeliveryAddress);
		}
		if (clientOrder.getOrdemItems().size() == 0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(noProducts);
		}
		for (OrderItem p : clientOrder.getOrdemItems()) {
			Optional<Product> optProduct = productRepository.findByName(p.getProduct().getName());
			if (!optProduct.isPresent()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(productNotIndb);
			}
			if (!optProduct.get().getName().equals(p.getProduct().getName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(productNotIndb);
			}
		}
		clientOrder.setCustomer(optCustomer.get());
		clientOrder.setTotal(0);
		clientOrder.setDate(new Date().toString());
		clientOrder.setLastUpdate(new Date().toString());
		clientOrder.setStatus("Order requested");

		Collection<OrderItem> intList = new ArrayList<>(clientOrder.getOrdemItems());
		clientOrder.setOrdemItems(new ArrayList<>());

		clientOrder = clientOrderRepository.save(clientOrder);

		ClientOrder clientOrderupdate = clientOrderRepository.getOne(clientOrder.getId());
		for (OrderItem p : intList) {
			Optional<Product> optProduct = productRepository.findByName(p.getProduct().getName());
			p.setProduct(optProduct.get());
			clientOrderupdate.setTotal((p.getProduct().getPrice() * p.getQuantity()) + clientOrder.getTotal());
			p.setTotal(p.getTotal() + (p.getProduct().getPrice() * p.getQuantity()));
			p.setClientOrder(clientOrder);
			orderItemRepository.save(p);
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Sucess");

	}

	@GetMapping("/api/v1/Order/{orderId}")
	@ResponseBody
	public ResponseEntity<String> getOrderById(@PathVariable("orderId") Integer orderId) {

		Optional<ClientOrder> optClientOrder = clientOrderRepository.findById(orderId);
		if (optClientOrder.isPresent()) {

			ObjectMapper mapper = new ObjectMapper();
			SimpleModule module = new SimpleModule("CustomCarSerializer", new Version(1, 0, 0, null, null, null));
			module.addSerializer(ClientOrder.class, new CustomClientOrderSerializer());
			mapper.registerModule(module);
			String clientOrderJson;
			try {
				clientOrderJson = mapper.writeValueAsString(optClientOrder.get());

			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
			}
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(clientOrderJson);
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);

	}

	@PostMapping("/api/v1/Order/customer/")
	@ResponseBody
	public ResponseEntity<String> getProductContainName(@RequestBody Customer customer) {
		String clientOrderJson=null;
		if (customer == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		Optional<Customer> optCustomer = customerRepository.findByEmail(customer.getEmail());
		if (!optCustomer.isPresent()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
//		List<String> clientOrderJsons = new ArrayList<>();
		
		for (ClientOrder clientOrder : optCustomer.get().getListaOrden()) {

			ObjectMapper mapper = new ObjectMapper();
			SimpleModule module = new SimpleModule("CustomCarSerializer", new Version(1, 0, 0, null, null, null));
			module.addSerializer(ClientOrder.class, new CustomClientOrderSerializer());
			mapper.registerModule(module);
			
			try {
				clientOrderJson = mapper.writeValueAsString(clientOrder);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
			}
//			clientOrderJsons.add(clientOrderJson);
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(clientOrderJson);
	}

}
