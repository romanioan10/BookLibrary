package Domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Rent extends Entity implements Serializable
{
    private int id;
    private User user;
    private BookCopy book;
    private LocalDate startDate;
    private LocalDate endDate;

    private String status;

    public enum Status {ACTIVE, COMPLETED, CANCELLED};

    public Rent(int id, User user, BookCopy book , LocalDate startDate, LocalDate endDate, String status) {
        super(id);
        this.user = user;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Rent(User user, BookCopy book , LocalDate startDate, LocalDate endDate, String status) {
        this.user = user;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public BookCopy getBookCopy() {
        return book;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setBookCopy(BookCopy book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "Rent{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", book=" + book.getBook().getTitle() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }

}
