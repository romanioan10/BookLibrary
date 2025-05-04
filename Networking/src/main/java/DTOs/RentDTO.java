package DTOs;

import Domain.Rent;

import java.io.Serializable;
import java.time.LocalDate;

public class RentDTO implements Serializable {
    private int id;
    private UserDTO userDTO;
    private BookCopyDTO bookCopyDTO;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public RentDTO(int id, UserDTO userDTO, BookCopyDTO bookCopyDTO,
                   LocalDate startDate, LocalDate endDate, String status) {
        this.id = id;
        this.userDTO = userDTO;
        this.bookCopyDTO = bookCopyDTO;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public BookCopyDTO getBookCopyDTO() {
        return bookCopyDTO;
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

    @Override
    public String toString() {
        return "RentDTO{" +
                "id=" + id +
                ", userDTO=" + userDTO +
                ", bookCopyDTO=" + bookCopyDTO +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                '}';
    }
}

