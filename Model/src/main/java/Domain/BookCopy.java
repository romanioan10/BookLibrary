package Domain;

import java.io.Serializable;

public class BookCopy extends Entity implements Serializable {
    private Book book;
    private Status status;

    public enum Status {AVAILABLE, BORROWED};

    // Constructor

    public BookCopy(int id, Book book) {
        super(id);
        this.book = book;
        this.status = Status.AVAILABLE;
    }

    public BookCopy(Book book) {
        this.book = book;
        this.status = Status.AVAILABLE;
    }

    public BookCopy(Book book, Status status) {
        this.book = book;
        this.status = status;
    }

    public BookCopy(int id, Book book, Status status) {
        super(id);
        this.book = book;
        this.status = status;
    }

    public BookCopy(int id) {
        super(id);
    }
    // Getters
    public Integer getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public Status getStatus() {
        return status;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // Mark as borrowed
    public void borrow() {
        if (this.status == Status.AVAILABLE) {
            this.status = Status.BORROWED;
        } else {
            throw new IllegalStateException("This copy is already borrowed.");
        }
    }

    // Mark as returned
    public void returnBook() {
        if (this.status == Status.BORROWED) {
            this.status = Status.AVAILABLE;
        } else {
            throw new IllegalStateException("This copy is already available.");
        }


    }

    @Override
    public String toString() {
        return "BookCopy{id=" + id + ", book=" + book.getTitle() + ", status=" + status + "}";
    }
}
