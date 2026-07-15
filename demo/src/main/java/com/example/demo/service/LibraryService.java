package com.example.demo.service;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.model.Student;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final StudentRepository studentRepository;
    private final IssueRepository issueRepository;

    @Autowired
    public LibraryService(BookRepository bookRepository,
                          AuthorRepository authorRepository,
                          StudentRepository studentRepository,
                          IssueRepository issueRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.studentRepository = studentRepository;
        this.issueRepository = issueRepository;
    }

    @Transactional
    public void addBook(String isbn, String title, String category, String authorName, String authorEmail, int quantity) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        Optional<Book> existingBookOpt = bookRepository.findById(isbn);
        if (existingBookOpt.isPresent()) {
            Book existingBook = existingBookOpt.get();
            existingBook.setQuantity(existingBook.getQuantity() + quantity);
            bookRepository.save(existingBook);
        } else {
            Book newBook = new Book(isbn, title, category, quantity);
            Author author = new Author(authorName, authorEmail, newBook);
            authorRepository.save(author);
        }
    }

    public Optional<Book> searchBookByTitle(String title) {
        return bookRepository.findByTitleIgnoreCase(title);
    }

    public List<Book> searchBookByCategory(String category) {
        return bookRepository.findByCategoryIgnoreCase(category);
    }

    public Optional<Book> searchBookByAuthor(String authorName) {
        Optional<Author> authorOpt = authorRepository.findByNameIgnoreCase(authorName);
        return authorOpt.map(Author::getAuthorBook);
    }

    public List<Author> listAllBooksWithAuthors() {
        return authorRepository.findAll();
    }

    @Transactional
    public Issue issueBook(String studentUsn, String studentName, String isbn) {
        if (studentUsn == null || studentUsn.trim().isEmpty()) {
            throw new IllegalArgumentException("Student USN cannot be empty");
        }
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book with ISBN " + isbn + " not found"));

        if (book.getQuantity() <= 0) {
            throw new IllegalStateException("Book is currently unavailable (out of stock)");
        }

        // Find or create Student
        Student student = studentRepository.findById(studentUsn)
                .orElseGet(() -> studentRepository.save(new Student(studentUsn, studentName)));

        // Decrement book quantity
        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);

        // Calculate dates
        Calendar cal = Calendar.getInstance();
        Date issueDateObj = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date returnDateObj = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String issueDateStr = sdf.format(issueDateObj) + "000";
        String returnDateStr = sdf.format(returnDateObj) + "000";

        Issue issue = new Issue(issueDateStr, returnDateStr, student, book);
        return issueRepository.save(issue);
    }

    public List<Issue> listBooksByUsn(String usn) {
        return issueRepository.findByIssueStudentUsn(usn);
    }

    public List<Issue> listBooksToReturnToday() {
        List<Issue> allIssues = issueRepository.findAll();
        List<Issue> todayIssues = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(new Date());

        for (Issue issue : allIssues) {
            if (issue.getReturnDate() != null && issue.getReturnDate().startsWith(todayStr)) {
                todayIssues.add(issue);
            }
        }
        return todayIssues;
    }
}
