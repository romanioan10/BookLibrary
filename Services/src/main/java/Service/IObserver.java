package Service;

import Domain.BookCopy;
import Domain.Rent;

import java.util.Collection;

public interface IObserver
{
    void updateBookCopies(Iterable<Integer> bookCopies) throws AppException;

    void updateRents(Collection<Rent> rents) throws AppException;
}
