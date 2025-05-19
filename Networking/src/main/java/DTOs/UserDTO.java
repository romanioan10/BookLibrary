package DTOs;

import java.io.Serializable;

public class UserDTO implements Serializable
{
    public int id;
    public String username;
    public String password;
    public String name;
    public String phone;
    public int role;

    public UserDTO(int id, String username, String password, String name, String phone, int role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }

    @Override
    public String toString() {
        return
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getPhone()
    {
        return phone;
    }

    public int getRole()
    {
        return role;
    }

    public String getUsername()
    {
        return username;
    }
}
