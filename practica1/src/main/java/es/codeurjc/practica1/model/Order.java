package es.codeurjc.practica1.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private double totalPrice;

	@ManyToOne  // Un usuario puede tener múltiples órdenes
	@JoinColumn(name = "owner_id")  // Especifica la clave foránea en la tabla "customer_order"
    @OnDelete(action = OnDeleteAction.CASCADE)
	private User owner;

    @ManyToMany
    private List<Product> products;

    protected Order() {
    }

    public Order(User owner, List<Product> products) {
        this.owner = owner;
        this.products = products != null ? products : new ArrayList<>();
        this.totalPrice = calculateTotalPrice();
        this.products = new ArrayList<>();
    }

    public Order(User owner, Product product) {
        this.owner = owner;
        this.products = new ArrayList<>();
        if (product != null) {
            this.products.add(product);
        }
        this.totalPrice = calculateTotalPrice();
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

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        this.totalPrice = calculateTotalPrice(); // Recalcular precio al actualizar productos
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void deleteAllProducts() {
        this.products.clear();
        this.totalPrice = 0; // Resetear el precio si se eliminan los productos
    }

    // Método para calcular el precio total basado en los productos
    private double calculateTotalPrice() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }

    public void addProduct(Product p){
        this.products.add(p);
    }
}
