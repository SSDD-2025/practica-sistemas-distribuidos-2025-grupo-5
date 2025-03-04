package es.codeurjc.practica1.model;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    
    private String name;
    private String email;
    private String password;
    private int rol; // 0 = Admin, 1 = User, 2 = Company.
    private int phoneNumber;

    // Un usuario puede crear muchas reviews
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")  
    private List<Product> products; 

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Order order;

    protected User() {}

    public User(String name, String email, String password, int rol, int phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.phoneNumber = phoneNumber;
        this.products = new ArrayList<>();  
        this.reviews = new ArrayList<>();
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
    }
    
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getRol() {
        return rol;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void deleteReview(Review review) {
        this.reviews.remove(review);
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void deleteAllReviews() {
        this.reviews.clear();
    }

    public void deleteAllProducts() {
        this.products.clear();
    }

    public void deleteOrder() {
        this.order = null;
    }


    public void deleteOrder(Order order) {
        this.order = null;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    public Order getOrders() {
        return this.order;
    }
}
