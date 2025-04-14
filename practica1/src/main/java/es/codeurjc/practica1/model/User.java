package es.codeurjc.practica1.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private boolean deleted; 
    private String name;
    private String email;
    private String encodedPassword;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    private int phoneNumber;

    // Un usuario puede crear muchas reviews
    @OneToMany(mappedBy = "author")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<Product> products;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    protected User() {
    }

    public User(String name, String email, String password, List<String> roles, int phoneNumber) {
        this.name = name;
        this.email = email;
        this.encodedPassword = password;
        this.roles = roles;
        this.deleted = false;
        this.phoneNumber = phoneNumber;
        this.products = new ArrayList<>();
        this.reviews = new ArrayList<>();
        this.orders = new ArrayList<>();
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
        return encodedPassword;
    }

    public List<String> getRoles() {
        return roles;
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
        this.encodedPassword = password;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
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
        this.orders.clear();
    }

    public void deleteOrder(Order order) {
        this.orders.remove(order);
       // this.orders.setAuthor(null);
    }


    public List<Order> getOrders() {
        return this.orders;
    }

    public void addAnOrder(Order order){
        this.orders.add(order);
    }

    public void setDeletedd(boolean deleted){
        this.deleted = deleted;
    }

    public boolean getDeletedd(){
        return this.deleted;
    }
}
