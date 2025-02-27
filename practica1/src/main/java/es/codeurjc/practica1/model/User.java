package es.codeurjc.practica1.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private int rol; // 0 = Admin, 1 = User, 2 = Empresa
    private int phoneNumber;

    //Constructor para cargar desde la BBDD
    protected User() {
    }

    public User(String name, String email, String password, int rol, int phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.phoneNumber = phoneNumber;
        
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

}
