package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "student")
public class Student {

    @Id
    private String usn;
    private String name;

    // No-arg constructor required by JPA
    public Student() {
    }

    // Parameterized constructor
    public Student(String usn, String name) {
        this.usn = usn;
        this.name = name;
    }

    // Getters and Setters
    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
