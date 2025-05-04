package Repository;

import Domain.Book;
import Domain.BookCopy;
import Interfaces.IBookRepository;
import Utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class BookRepository implements IBookRepository
{
    private JdbcUtils dbUtils;

    public BookRepository(Properties props) {
        this.dbUtils = new JdbcUtils(props);
    }

    @Override
    public void add(Book entitate)
    {
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("insert into Books (title, author) values (?, ?)")) {
            preStmt.setString(1, entitate.getTitle());
            preStmt.setString(2, entitate.getAuthor());
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }


    }

    @Override
    public void update(Integer integer, Book entitate)
    {
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("update Books set title=?, author=? where id_book=?")) {
            preStmt.setInt(3, integer);
            preStmt.setString(1, entitate.getTitle());
            preStmt.setString(2, entitate.getAuthor());
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

    }

    @Override
    public void remove(Integer integer)
    {
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("delete from Books where id=?")) {
            preStmt.setInt(1, integer);
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

    }

    @Override
    public Book findOne(Integer integer)
    {
        Connection con = dbUtils.getConnection();
        Book book = null;

        try (PreparedStatement preStmt = con.prepareStatement("select * from Books where id_book=?")) {
            preStmt.setInt(1, integer);
            var rs = preStmt.executeQuery();
            if (rs.next()) {
                book = new Book(rs.getInt("id_book"), rs.getString("title"), rs.getString("author"));
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

        return book;
    }

    @Override
    public Iterable<Book> getAll()
    {
        Connection con = dbUtils.getConnection();
        var books = new ArrayList<Book>();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Books")) {
            var rs = preStmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(rs.getInt("id_book"), rs.getString("title"), rs.getString("author")));
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

        return books;
    }

    @Override
    public void setAll(Iterable<Book> entitati)
    {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from Books")) {
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

        for (Book book : entitati) {
            add(book);
        }

    }

    @Override
    public Book findOne(int id) {
        Connection con = dbUtils.getConnection();
        Book book = null;

        try (PreparedStatement preStmt = con.prepareStatement("select * from Books where id=?")) {
            preStmt.setInt(1, id);
            var rs = preStmt.executeQuery();
            if (rs.next()) {
                book = new Book(rs.getInt("id_book"), rs.getString("title"), rs.getString("author"));
            }
        } catch (SQLException e) {
            System.out.println("eroare baaaaaaaaaaaa " + e);
        }

        return book;
    }



}
