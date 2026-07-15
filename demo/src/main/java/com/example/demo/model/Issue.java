package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "issue")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer issueId;

    private String issueDate;
    private String returnDate;

    @OneToOne
    @JoinColumn(name = "student_usn", referencedColumnName = "usn")
    private Student issueStudent;

    @OneToOne
    @JoinColumn(name = "book_isbn", referencedColumnName = "isbn")
    private Book issueBook;

    // No-arg constructor required by JPA
    public Issue() {
    }

    // Parameterized constructor
    public Issue(String issueDate, String returnDate, Student issueStudent, Book issueBook) {
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.issueStudent = issueStudent;
        this.issueBook = issueBook;
    }

    // Getters and Setters
    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public Student getIssueStudent() {
        return issueStudent;
    }

    public void setIssueStudent(Student issueStudent) {
        this.issueStudent = issueStudent;
    }

    public Book getIssueBook() {
        return issueBook;
    }

    public void setIssueBook(Book issueBook) {
        this.issueBook = issueBook;
    }
}
