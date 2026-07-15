package com.example.demo.menu;

import com.example.demo.model.Author;
import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class LibraryMenu {

    private final LibraryService libraryService;

    @Autowired
    public LibraryMenu(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("================================================");
        System.out.println("      Welcome to the IronLibrary System!        ");
        System.out.println("================================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readIntInput(scanner, "Enter your choice: ");
            System.out.println();
            try {
                switch (choice) {
                    case 1:
                        handleAddBook(scanner);
                        break;
                    case 2:
                        handleSearchByTitle(scanner);
                        break;
                    case 3:
                        handleSearchByCategory(scanner);
                        break;
                    case 4:
                        handleSearchByAuthor(scanner);
                        break;
                    case 5:
                        handleListAllBooks();
                        break;
                    case 6:
                        handleIssueBook(scanner);
                        break;
                    case 7:
                        handleListBooksByUsn(scanner);
                        break;
                    case 8:
                        handleListBooksReturnedToday();
                        break;
                    case 9:
                        System.out.println("Thank you for using IronLibrary. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please select a number between 1 and 9.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("Menu Options:");
        System.out.println("1. Add a book");
        System.out.println("2. Search book by title");
        System.out.println("3. Search book by category");
        System.out.println("4. Search book by Author");
        System.out.println("5. List all books along with author");
        System.out.println("6. Issue book to student");
        System.out.println("7. List books by usn");
        System.out.println("8. List books to be returned today (Bonus)");
        System.out.println("9. Exit");
        System.out.println();
    }

    private void handleAddBook(Scanner scanner) {
        System.out.print("Enter isbn : ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Enter title : ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter category : ");
        String category = scanner.nextLine().trim();
        System.out.print("Enter Author name : ");
        String authorName = scanner.nextLine().trim();
        System.out.print("Enter Author mail : ");
        String authorEmail = scanner.nextLine().trim();
        int quantity = readIntInput(scanner, "Enter number of books : ");

        if (isbn.isEmpty() || title.isEmpty() || authorName.isEmpty() || authorEmail.isEmpty()) {
            System.out.println("Error: Fields cannot be empty.");
            return;
        }

        libraryService.addBook(isbn, title, category, authorName, authorEmail, quantity);
        System.out.println("Book and Author successfully added to the system!");
    }

    private void handleSearchByTitle(Scanner scanner) {
        System.out.print("Enter title : ");
        String title = scanner.nextLine().trim();
        Optional<Book> bookOpt = libraryService.searchBookByTitle(title);

        if (bookOpt.isPresent()) {
            System.out.println();
            printBookHeader();
            printBookRow(bookOpt.get());
        } else {
            System.out.println("No book found with title: " + title);
        }
    }

    private void handleSearchByCategory(Scanner scanner) {
        System.out.print("Enter category : ");
        String category = scanner.nextLine().trim();
        List<Book> books = libraryService.searchBookByCategory(category);

        if (!books.isEmpty()) {
            System.out.println();
            printBookHeader();
            for (Book book : books) {
                printBookRow(book);
            }
        } else {
            System.out.println("No books found in category: " + category);
        }
    }

    private void handleSearchByAuthor(Scanner scanner) {
        System.out.print("Enter name : ");
        String authorName = scanner.nextLine().trim();
        Optional<Book> bookOpt = libraryService.searchBookByAuthor(authorName);

        if (bookOpt.isPresent()) {
            System.out.println();
            printBookHeader();
            printBookRow(bookOpt.get());
        } else {
            System.out.println("No book found for Author: " + authorName);
        }
    }

    private void handleListAllBooks() {
        List<Author> authors = libraryService.listAllBooksWithAuthors();
        if (!authors.isEmpty()) {
            printAllBooksHeader();
            for (Author author : authors) {
                printAllBooksRow(author);
            }
        } else {
            System.out.println("No books in the library.");
        }
    }

    private void handleIssueBook(Scanner scanner) {
        System.out.print("Enter usn : ");
        String usn = scanner.nextLine().trim();
        System.out.print("Enter name : ");
        String studentName = scanner.nextLine().trim();
        System.out.print("Enter book ISBN : ");
        String isbn = scanner.nextLine().trim();

        if (usn.isEmpty() || studentName.isEmpty() || isbn.isEmpty()) {
            System.out.println("Error: Input details cannot be empty.");
            return;
        }

        try {
            Issue issue = libraryService.issueBook(usn, studentName, isbn);
            // Format output return date in standard EEST/local format as requested by matching Aug 01 19:45:40 EEST 2022
            // We stored formatted db string in returnDate. Let's parse it and print in EEST style format:
            SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date returnDate = dbSdf.parse(issue.getReturnDate());
            System.out.println();
            System.out.println("Book issued. Return date : " + returnDate.toString());
        } catch (ParseException e) {
            System.out.println("Book issued, but could not format return date correctly.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleListBooksByUsn(Scanner scanner) {
        System.out.print("Enter usn : ");
        String usn = scanner.nextLine().trim();
        List<Issue> issues = libraryService.listBooksByUsn(usn);

        if (!issues.isEmpty()) {
            System.out.println();
            printIssueHeader();
            for (Issue issue : issues) {
                printIssueRow(issue);
            }
        } else {
            System.out.println("No rented books found for USN: " + usn);
        }
    }

    private void handleListBooksReturnedToday() {
        List<Issue> issues = libraryService.listBooksToReturnToday();
        if (!issues.isEmpty()) {
            System.out.println();
            printIssueHeader();
            for (Issue issue : issues) {
                printIssueRow(issue);
            }
        } else {
            System.out.println("No books are scheduled to be returned today.");
        }
    }

    // Helper functions for printing tables
    private void printBookHeader() {
        System.out.printf("%-24s%-20s%-12s%s%n", "Book ISBN", "Book Title", "Category", "No of Books");
    }

    private void printBookRow(Book book) {
        System.out.printf("%-24s%-20s%-12s%d%n", book.getIsbn(), book.getTitle(), book.getCategory(), book.getQuantity());
    }

    private void printAllBooksHeader() {
        System.out.printf("%-24s%-20s%-12s%-16s%-20s%s%n", "Book ISBN", "Book Title", "Category", "No of Books", "Author name", "Author mail");
    }

    private void printAllBooksRow(Author author) {
        Book book = author.getAuthorBook();
        System.out.printf("%-24s%-20s%-12s%-16d%-20s%s%n",
                book.getIsbn(), book.getTitle(), book.getCategory(), book.getQuantity(),
                author.getName(), author.getEmail());
    }

    private void printIssueHeader() {
        System.out.printf("%-20s%-16s%s%n", "Book Title", "Student Name", "Return date");
    }

    private void printIssueRow(Issue issue) {
        System.out.printf("%-20s%-16s%s%n",
                issue.getIssueBook().getTitle(),
                issue.getIssueStudent().getName(),
                issue.getReturnDate());
    }

    private int readIntInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }
}
