package com.example.demo.service;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LibraryServiceTest {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private IssueRepository issueRepository;

    @BeforeEach
    public void setUp() {
        // Clear repositories before each test
        issueRepository.deleteAll();
        authorRepository.deleteAll();
        bookRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    public void testAddBook_NewBook() {
        libraryService.addBook("123-456", "Test Book", "Education", "Author Name", "author@test.com", 5);

        Optional<Book> bookOpt = bookRepository.findById("123-456");
        assertTrue(bookOpt.isPresent());
        assertEquals("Test Book", bookOpt.get().getTitle());
        assertEquals("Education", bookOpt.get().getCategory());
        assertEquals(5, bookOpt.get().getQuantity());

        Optional<Author> authorOpt = authorRepository.findByName("Author Name");
        assertTrue(authorOpt.isPresent());
        assertEquals("author@test.com", authorOpt.get().getEmail());
        assertEquals("123-456", authorOpt.get().getAuthorBook().getIsbn());
    }

    @Test
    public void testAddBook_ExistingBook_IncreasesQuantity() {
        libraryService.addBook("123-456", "Test Book", "Education", "Author Name", "author@test.com", 5);
        libraryService.addBook("123-456", "Test Book", "Education", "Author Name", "author@test.com", 3);

        Optional<Book> bookOpt = bookRepository.findById("123-456");
        assertTrue(bookOpt.isPresent());
        assertEquals(8, bookOpt.get().getQuantity());
    }

    @Test
    public void testAddBook_InvalidArgs_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            libraryService.addBook("", "Title", "Cat", "Author", "mail", 5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            libraryService.addBook("123", "", "Cat", "Author", "mail", 5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            libraryService.addBook("123", "Title", "Cat", "Author", "mail", -1);
        });
    }

    @Test
    public void testSearchBookByTitle() {
        libraryService.addBook("123-456", "Test Book", "Education", "Author Name", "author@test.com", 5);

        Optional<Book> bookOpt = libraryService.searchBookByTitle("test book");
        assertTrue(bookOpt.isPresent());
        assertEquals("123-456", bookOpt.get().getIsbn());

        Optional<Book> notFoundOpt = libraryService.searchBookByTitle("Unknown");
        assertFalse(notFoundOpt.isPresent());
    }

    @Test
    public void testSearchBookByCategory() {
        libraryService.addBook("123-456", "Test Book 1", "Education", "Author Name 1", "author1@test.com", 5);
        libraryService.addBook("789-012", "Test Book 2", "fiction", "Author Name 2", "author2@test.com", 2);

        List<Book> educationBooks = libraryService.searchBookByCategory("EDUCATION");
        assertEquals(1, educationBooks.size());
        assertEquals("123-456", educationBooks.get(0).getIsbn());

        List<Book> emptyBooks = libraryService.searchBookByCategory("Romance");
        assertTrue(emptyBooks.isEmpty());
    }

    @Test
    public void testSearchBookByAuthor() {
        libraryService.addBook("123-456", "Test Book", "Education", "Nicholas Sparks", "nicholas@test.com", 5);

        Optional<Book> bookOpt = libraryService.searchBookByAuthor("nicholas sparks");
        assertTrue(bookOpt.isPresent());
        assertEquals("123-456", bookOpt.get().getIsbn());

        Optional<Book> notFoundOpt = libraryService.searchBookByAuthor("Unknown");
        assertFalse(notFoundOpt.isPresent());
    }

    @Test
    public void testListAllBooksWithAuthors() {
        libraryService.addBook("123", "Book 1", "Cat 1", "Author 1", "mail 1", 2);
        libraryService.addBook("456", "Book 2", "Cat 2", "Author 2", "mail 2", 3);

        List<Author> authors = libraryService.listAllBooksWithAuthors();
        assertEquals(2, authors.size());
    }

    @Test
    public void testIssueBook_Success() {
        libraryService.addBook("123-456", "Test Book", "Education", "Author Name", "author@test.com", 2);

        Issue issue = libraryService.issueBook("STUDENT1", "John Doe", "123-456");

        assertNotNull(issue);
        assertEquals("STUDENT1", issue.getIssueStudent().getUsn());
        assertEquals("123-456", issue.getIssueBook().getIsbn());

        // Book quantity should decrement
        Optional<Book> bookOpt = bookRepository.findById("123-456");
        assertTrue(bookOpt.isPresent());
        assertEquals(1, bookOpt.get().getQuantity());

        // Return date should be 7 days from now
        assertNotNull(issue.getReturnDate());
        assertTrue(issue.getReturnDate().length() > 0);
    }

    @Test
    public void testIssueBook_BookNotFound_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            libraryService.issueBook("STUDENT1", "John Doe", "NON-EXISTENT");
        });
    }

    @Test
    public void testIssueBook_OutOfStock_ThrowsException() {
        libraryService.addBook("123-456", "Test Book", "Education", "Author Name", "author@test.com", 0);

        assertThrows(IllegalStateException.class, () -> {
            libraryService.issueBook("STUDENT1", "John Doe", "123-456");
        });
    }

    @Test
    public void testListBooksByUsn() {
        libraryService.addBook("123", "Book 1", "Cat 1", "Author 1", "mail 1", 2);
        libraryService.issueBook("STUDENT1", "John Doe", "123");

        List<Issue> issues = libraryService.listBooksByUsn("STUDENT1");
        assertEquals(1, issues.size());
        assertEquals("123", issues.get(0).getIssueBook().getIsbn());
        assertEquals("John Doe", issues.get(0).getIssueStudent().getName());
    }

    @Test
    public void testListBooksToReturnToday() {
        libraryService.addBook("123", "Book 1", "Cat 1", "Author 1", "mail 1", 2);
        Issue issue = libraryService.issueBook("STUDENT1", "John Doe", "123");

        // The issue is due in 7 days, so listBooksToReturnToday should be empty
        List<Issue> todayIssues = libraryService.listBooksToReturnToday();
        assertTrue(todayIssues.isEmpty());

        // Now let's manually alter return date to today's date in db representation
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String todayReturnDate = sdf.format(new Date()) + "000";
        issue.setReturnDate(todayReturnDate);
        issueRepository.save(issue);

        List<Issue> todayIssuesModified = libraryService.listBooksToReturnToday();
        assertEquals(1, todayIssuesModified.size());
        assertEquals("123", todayIssuesModified.get(0).getIssueBook().getIsbn());
    }
}
