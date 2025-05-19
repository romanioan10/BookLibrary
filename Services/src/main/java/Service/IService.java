package Service;

import Domain.BookCopy;
import Domain.Rent;
import Domain.User;

import java.time.LocalDate;
import java.util.Collection;

public interface IService
{

    User login(User user, IObserver client) throws AppException;


    public void logout(User user) throws AppException;
    public Iterable<Integer> getAllBookCopies() throws AppException;
    public Iterable<Rent> getAllRents() throws AppException;

    int getRentCountForUser(User user);

    public void rentBook(User user, BookCopy bookCopy, LocalDate startDate, LocalDate endDate, String  status) throws AppException;
    public void addRent(Rent rent) throws AppException;
    BookCopy getBookCopyById(int id) throws AppException;

    void returnBook(int rentId) throws AppException;

}
