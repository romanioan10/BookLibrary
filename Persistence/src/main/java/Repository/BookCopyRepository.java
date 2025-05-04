package Repository;

import Domain.Book;
import Domain.BookCopy;
import Interfaces.IBookCopyRepository;
import Utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BookCopyRepository implements IBookCopyRepository
{
    JdbcUtils dbUtils;
    public BookCopyRepository(Properties props) {
        this.dbUtils = new JdbcUtils(props);
    }
    @Override
    public List<BookCopy> findByBookId(Integer bookId)
    {
        Connection con = dbUtils.getConnection();
        List<BookCopy> bookCopies = new ArrayList<>();

        try (PreparedStatement preStmt = con.prepareStatement("select * from BookCopy where id_book_copy=?")) {
            preStmt.setInt(1, bookId);
            ResultSet rs = preStmt.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("id_book_copy");
                Book book = getBookById(id);
                String status = rs.getString("status");
                BookCopy bookCopy = new BookCopy(book, BookCopy.Status.valueOf(status));
                bookCopies.add(bookCopy);
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }
        return bookCopies;
    }

    @Override
    public List<BookCopy> findByStatus(String status) {
        Connection con = dbUtils.getConnection();
        List<BookCopy> bookCopies = new ArrayList<>();

        try (PreparedStatement preStmt = con.prepareStatement("select * from BookCopy where status=?")) {
            preStmt.setString(1, status);
            ResultSet rs = preStmt.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("id_book_copy");

                int bookId = rs.getInt("id_book");  //  OBȚINE id_book, NU id_book_copy
                Book book = getBookById(bookId);    //  Aici era greșeala: foloseai id_book_copy

                BookCopy bookCopy = new BookCopy(id, book, BookCopy.Status.valueOf(status));
                bookCopies.add(bookCopy);
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }
        return bookCopies;
    }


    @Override
    public BookCopy findOne(Integer id) {
        Connection con = dbUtils.getConnection();
        BookCopy bookCopy = null;

        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM BookCopy WHERE id_book_copy = ?")) {
            preStmt.setInt(1, id);
            ResultSet rs = preStmt.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("id_book");
                String statusStr = rs.getString("status");

                Book book = getBookById(bookId);
                BookCopy.Status status = BookCopy.Status.valueOf(statusStr);

                // Folosește constructorul care setează ID-ul prin super(id)
                bookCopy = new BookCopy(id, book, status);

                System.out.println("DEBUG: BookCopy loaded with ID = " + bookCopy.getId());
            }

        } catch (SQLException e) {
            System.out.println("Error DB: " + e.getMessage());
        }

        return bookCopy;
    }






    @Override
    public void add(Integer entitate)
    {
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("insert into BookCopy (id_book, status) values (?, ?)")) {
            preStmt.setInt(1, entitate);
            preStmt.setString(2, BookCopy.Status.AVAILABLE.toString());
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

    }

    @Override
    public void update(BookCopy bookCopy, Integer id) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE BookCopy SET status = ? WHERE id_book_copy = ?")) {

            System.out.println("Updating BookCopy ID = " + id + ", intended status = " + bookCopy.getStatus());

            BookCopy before = findOne(id);
            if (before != null) {
                System.out.println("BEFORE UPDATE → ID: " + before.getId() + ", status = " + before.getStatus());
            } else {
                System.out.println("BookCopy not found before update!");
            }

            ps.setString(1, bookCopy.getStatus().toString());
            ps.setInt(2, id);
            int rows = ps.executeUpdate();

            System.out.println("Updated BookCopy ID " + id + " to status " + bookCopy.getStatus() + " (rows affected: " + rows + ")");

            BookCopy after = findOne(id);
            if (after != null) {
                System.out.println("AFTER UPDATE → ID: " + after.getId() + ", status = " + after.getStatus());
            }

        } catch (SQLException e) {
            System.err.println("Error updating BookCopy: " + e.getMessage());
        }
    }





    @Override
    public void remove(BookCopy bookCopy)
    {
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("delete from BookCopy where id_book_copy=?")) {
            preStmt.setInt(1, bookCopy.getBook().getId());
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

    }

    @Override
    public Integer findOne(BookCopy bookCopy) {
        Connection con = dbUtils.getConnection();
        Integer id = null;

        String sql = "SELECT id_book_copy FROM BookCopy WHERE id_book = ? AND status = ?";

        try (PreparedStatement preStmt = con.prepareStatement(sql)) {
            preStmt.setInt(1, bookCopy.getBook().getId());
            preStmt.setString(2, bookCopy.getStatus().toString());

            ResultSet rs = preStmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id_book_copy");
            } else {
                System.out.println(" No BookCopy found with given book and status.");
            }
        } catch (SQLException e) {
            System.out.println("Error DB in findOne(BookCopy): " + e.getMessage());
        }

        return id;
    }


    @Override
    public Iterable<Integer> getAll() {
        List<Integer> bookCopies = new ArrayList<>();
        String sql = "SELECT id_book_copy FROM BookCopy";

        try (Connection con = dbUtils.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bookCopies.add(rs.getInt("id_book_copy"));
            }

        } catch (SQLException e) {
            System.err.println("Database error in getAll(): " + e.getMessage());
            // Optional: throw new RuntimeException("Error retrieving book copies", e);
        }

        return bookCopies;
    }


    @Override
    public void setAll(Iterable<Integer> entitati) {

    }

    public Book getBookById(int id) {
        String sql = "SELECT * FROM Books WHERE id_book = ?";
        try (Connection connection = dbUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                return new Book(id, title, author);  // ✅ aici ID-ul vine corect din argument
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }




}
