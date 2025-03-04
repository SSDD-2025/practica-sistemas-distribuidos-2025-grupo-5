package es.codeurjc.practica1.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_order") // This is the name of the table in the database
public class Order{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    private double totalPrice;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User owner;

	@ManyToMany
	private List<Product> products;

	protected Order() {}

	public Order(User author, List<Product> product) {
		this.owner = author;
		this.products = product;
	}

	public Order(User author, Product product) {
		this.owner = author;
		this.products.add(product);
		this.totalPrice=0;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getOwner() {
		return owner;
	}

	public void setAuthor(User author) {
		this.owner = author;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}


    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

	public void setOrder(Order order){
		this.owner = order.getOwner();
	}

	public void deleteAllProducts(){
		this.products.clear();
	}

	
}