package es.codeurjc.practica1.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String img;
    private String provider;
    
    public Product() {
    }

    public Product(String name, String description, double price, int stock, String img, String provider) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.img = img;
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

    public String getImg() {
        return img;
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

    public void setImg(String img) {
        this.img = img;
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

}


