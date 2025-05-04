package Interfaces;

import Domain.BookCopy;

import java.util.List;


public interface IBookCopyRepository extends IRepository<BookCopy, Integer> {

    List<BookCopy> findByBookId(Integer bookId);
    List<BookCopy> findByStatus(String status);
    BookCopy findOne(Integer id);
}
