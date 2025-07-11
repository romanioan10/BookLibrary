package DTOs;

import Domain.Book;

import java.io.Serializable;


public class BookCopyDTO implements Serializable {
    public int id;
    public BookDTO book;
    public String status;


    public BookCopyDTO(int id, BookDTO book, String status) {
        this.id = id;
        this.book = book;
        this.status = status;
    }

    public int getId() {
        return id;
    }
    public String getStatus() {
        return status;
    }

    public BookDTO getBook() {
        return book;
    }

    @Override
    public String toString() {
        return
                "book=" + book +
                ", status='" + status + '\'';
    }


}

