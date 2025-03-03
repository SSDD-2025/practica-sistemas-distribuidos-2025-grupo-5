package es.codeurjc.practica1.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
    
@Entity
public class Order{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

    private double totalPrice;

	@ManyToOne
	@JoinColumn(name = "author_id")
	private User author;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private List<Product> products;

	protected Order() {}

	public Order(User author, List<Product> products) {
		this.author = author;
		this.products = products;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
    public void addProduct(Product product) {
        this.products.add(product);
    }
    public void deleteProduct(Product product) {
        this.products.remove(product);
    }
    public double getTotalPrice() {
        double totalPrice = 0;
        for (Product p : products) {
            totalPrice += p.getPrice();
        }
        this.setTotalPrice(totalPrice);
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}