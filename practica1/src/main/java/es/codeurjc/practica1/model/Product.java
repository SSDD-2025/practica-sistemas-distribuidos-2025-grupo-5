package es.codeurjc.practica1.model;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private double price;
    private int stock;
    private String provider;

    @Column(columnDefinition = "TEXT")
	private String description;

	@ManyToMany
	private List<User> users;

	@Lob
	private Blob imageFile;
	private int publicationYear;
	private String lang;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Review> reviews = new ArrayList<>();

    public Product() {}

    public Product(String name, String description, double price, int stock, String provider) { //Blob img
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        //this.imageFile = img;
        this.users = new ArrayList<>();
        this.provider = provider;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

	public List<User> getShops() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

    public Blob getImageFile() {
        return imageFile;
    }


    public String getProvider() {
        return provider;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setImageFile(Blob img) {
        this.imageFile = img;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    /* 
    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price + ", stock="
                + stock + ", img=" + img + "]";
    }
                */

    public void addReview(Review review) {
        reviews.add(review);    
    }
    public void removeReview(Review review) {
        reviews.remove(review);
    }
    public List<Review> getReviews() {
        return reviews;
    }   
}


