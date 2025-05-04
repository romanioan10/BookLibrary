package Interfaces;

import Domain.Book;

public interface IBookRepository extends IRepository<Integer, Book>
{
    Book findOne(int id);
}
