package DTOs;
import Domain.Book;

import java.io.Serializable;

public class BookDTO implements Serializable {
    private int id;
    private String title;
    private String author;

    public BookDTO(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public BookDTO(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return title + " - " + author;
    }


}
