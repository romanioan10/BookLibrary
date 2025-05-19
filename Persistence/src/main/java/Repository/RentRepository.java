package Repository;

import Domain.Book;
import Domain.BookCopy;
import Domain.Rent;
import Domain.User;
import Interfaces.IRentRepository;
import Utils.JdbcUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RentRepository implements IRentRepository
{
    JdbcUtils dbUtils;
    public RentRepository(Properties properties) {
        this.dbUtils = new JdbcUtils(properties);
    }

    @Override
    public void add(Rent rent) {
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement(
                "INSERT INTO Rent (id_user, id_book_copy, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            preStmt.setInt(1, rent.getUser().getId());
            preStmt.setInt(2, rent.getBookCopy().getId());
            preStmt.setDate(3, Date.valueOf(rent.getStartDate()));
            preStmt.setDate(4, Date.valueOf(rent.getEndDate()));
            preStmt.setString(5, rent.getStatus());

            int affectedRows = preStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Inserting rent failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    rent.setId(generatedId);  // setează ID-ul corect
                    System.out.println("Rent successfully added with ID: " + generatedId);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error DB in add(Rent): " + e);
        }
    }




    @Override
    public void update(Integer integer, Rent entitate)
    {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("update Rent set id_user=?, id_book_copy=?, start_date=?, end_date=?, status=? where id_rent=?")) {
            preStmt.setInt(1, entitate.getUser().getId());
            preStmt.setInt(2, entitate.getBookCopy().getId());
            preStmt.setDate(3, Date.valueOf(entitate.getStartDate()));
            preStmt.setDate(4, Date.valueOf(entitate.getEndDate()));
            preStmt.setString(5, entitate.getStatus());
            preStmt.setInt(5, integer);
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

    }

    @Override
    public void remove(Integer id) {
        String sql = "DELETE FROM Rent WHERE id_rent = ?";

        try (Connection con = dbUtils.getConnection();
             PreparedStatement preStmt = con.prepareStatement(sql)) {

            preStmt.setInt(1, id);
            int rowsAffected = preStmt.executeUpdate();

            if (rowsAffected == 0) {
                System.err.println("[RentRepository]  No rent found with ID: " + id);
            } else {
                System.out.println("[RentRepository] Rent with ID " + id + " removed successfully.");
            }

        } catch (SQLException e) {
            System.err.println("[RentRepository] Error deleting rent: " + e.getMessage());
            throw new RuntimeException("Error deleting rent with ID " + id, e);
        }
    }


    @Override
    public Rent findOne(Integer integer)
    {
        Connection con = dbUtils.getConnection();
        Rent rent = null;
        try (PreparedStatement preStmt = con.prepareStatement("select * from Rent where id_rent=?")) {
            preStmt.setInt(1, integer);
            ResultSet resultSet = preStmt.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt("id_user");
                int bookCopyId = resultSet.getInt("id_book_copy");
                LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                String status = resultSet.getString("status");
                User user = getUserById(userId);
                BookCopy bookCopy = getBookCopyById(bookCopyId);
                rent = new Rent(user, bookCopy, startDate, endDate, status);
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }
        return rent;
    }

    @Override
    public Iterable<Rent> getAll() {
        Connection con = dbUtils.getConnection();
        List<Rent> rents = new ArrayList<>();

        try (PreparedStatement preStmt = con.prepareStatement(
                "SELECT id_rent, id_user, id_book_copy, start_date, end_date, status FROM Rent"
        );) {
            ResultSet resultSet = preStmt.executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt("id_user");
                int bookCopyId = resultSet.getInt("id_book_copy");
                LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
                LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
                String status = resultSet.getString("status");

                User user = getUserById(userId);
                BookCopy bookCopy = getBookCopyById(bookCopyId);  // trebuie să aibă și cartea setată!

                if (bookCopy.getBook() == null) {
                    System.out.println("Warning: book in bookCopy is null for ID " + bookCopyId);
                }

                int id = resultSet.getInt("id_rent");  //  Verifică dacă există exact această coloană
                Rent rent = new Rent(user, bookCopy, startDate, endDate, status);
                rent.setId(id);
                System.out.println("↪ ResultSet ID = " + id);  // TREBUIE să fie != 0

                rents.add(rent);
            }
        } catch (SQLException e) {
            System.out.println("Error DB: " + e);
        }
        return rents;
    }




    @Override
    public void setAll(Iterable<Rent> entitati) {

    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM User WHERE id_user = ?";
        try (Connection connection = dbUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                User user = new User(id);
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getInt("role"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public BookCopy getBookCopyById(int id) {
        String sql = "SELECT * FROM BookCopy WHERE id_book_copy = ?";
        try (Connection connection = dbUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int bookId = resultSet.getInt("id_book");
                Book book = getBookById(bookId);
                if (book == null) {
                    System.err.println("Book not found for BookCopy ID: " + id);
                    return null;
                }

                BookCopy copy = new BookCopy(book);
                copy.setId(id);
                copy.setStatus(BookCopy.Status.valueOf(resultSet.getString("status")));
                return copy;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Book getBookById(int id) {
        String sql = "SELECT * FROM Books WHERE id_book = ?";
        try (Connection con = dbUtils.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                return new Book(id, title, author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
