package es.codeurjc.practica1.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private boolean me;
	private String title;
	private String text;
	private List<String> comments;

	// Each review has a single author
	@ManyToOne
	@JoinColumn(name = "author_id")
	private User author;

	// Each review has a single product
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	protected Review() {
	}

	public Review(String title, String text, User author, Product product) {
		this.title = title;
		this.text = text;
		this.author = author;
		this.product = product;
		this.me=false;
		this.comments = new ArrayList<>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public boolean getme() {
		return me;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setme(boolean tellMe) {
		this.me = tellMe;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "Review [id=" + id + ", title=" + title + ", text=" + text + "]";
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
	}

	public void addComment(String comment) {
		this.comments.add(comment);
	}
	public void removeComment(String comment) {
		this.comments.remove(comment);
	}
	public void removeAllComments() {
		this.comments.clear();
	}

	


}
