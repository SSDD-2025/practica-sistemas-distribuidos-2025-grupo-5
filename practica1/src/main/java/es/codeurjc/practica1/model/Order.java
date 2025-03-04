package es.codeurjc.practica1.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

	@OneToOne
	@JoinColumn(name = "product_id")
	private Product product;

	protected Order() {}

	public Order(User author, Product product) {
		this.owner = author;
		this.product = product;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getAuthor() {
		return owner;
	}

	public void setAuthor(User author) {
		this.owner = author;
	}

	public Product getProducts() {
		return product;
	}

	public void setProducts(List<Product> products) {
		this.product = product;
	}


    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}