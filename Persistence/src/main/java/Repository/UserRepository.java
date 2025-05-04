package Repository;

import Domain.User;
import Interfaces.IUserRepository;
import Utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class UserRepository implements IUserRepository
{
    private JdbcUtils dbUtils;

    public UserRepository(Properties props) {
        this.dbUtils = new JdbcUtils(props);
    }


    @Override
    public void add(User entitate)
    {
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("insert into User (username, password, name, phone, role) values (?, ?, ?, ?, ?)")) {
            preStmt.setString(1, entitate.getUsername());
            preStmt.setString(2, entitate.getPassword());
            preStmt.setString(3, entitate.getName());
            preStmt.setString(4, entitate.getPhone());
            preStmt.setInt(5, entitate.getRole());
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }


    }

    @Override
    public void update(Integer integer, User entitate)
    {
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("update User set username=?, password=?, name=?, phone=?, role=? where id_user=?")) {
            preStmt.setInt(6, integer);
            preStmt.setString(1, entitate.getUsername());
            preStmt.setString(2, entitate.getPassword());
            preStmt.setString(3, entitate.getName());
            preStmt.setString(4, entitate.getPhone());
            preStmt.setInt(5, entitate.getRole());
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

    }

    @Override
    public void remove(Integer integer)
    {
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("delete from User where id_user=?")) {
            preStmt.setInt(1, integer);
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

    }

    @Override
    public User findOne(Integer integer)
    {
        Connection con = dbUtils.getConnection();
        User user = null;

        try (PreparedStatement preStmt = con.prepareStatement("select * from User where id_user=?")) {
            preStmt.setInt(1, integer);
            var rs = preStmt.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

        return user;

    }

    @Override
    public Iterable<User> getAll()
    {
        Connection con = dbUtils.getConnection();
        Collection<User> users = new ArrayList<>();
        try (PreparedStatement preStmt = con.prepareStatement("select * from User")) {
            var rs = preStmt.executeQuery();
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getInt("role")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

        return users;
    }

    @Override
    public void setAll(Iterable<User> entitati)
    {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("delete from User")) {
            preStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error DB " + e);
        }

        for (User user : entitati) {
            add(user);
        }

    }

    @Override
    public User findByUsername(String username) {
        try (Connection con = dbUtils.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM User WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_user"); // <-- AICI!
                String password = rs.getString("password");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                int role = rs.getInt("role");

                User user = new User(id);
                user.setUsername(username);
                user.setPassword(password);
                user.setName(name);
                user.setPhone(phone);
                user.setRole(role);
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
