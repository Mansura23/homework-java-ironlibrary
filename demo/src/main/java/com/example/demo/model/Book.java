package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "book")
public class Book {

    @Id
    private String isbn;
    private String title;
    private String category;
    private Integer quantity;

    // No-arg constructor required by JPA
    public Book() {
    }

    // Parameterized constructor
    public Book(String isbn, String title, String category, Integer quantity) {
        this.isbn = isbn;
        this.title = title;
        this.category = category;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
