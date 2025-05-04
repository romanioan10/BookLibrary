package Interfaces;

import Domain.User;

public interface IUserRepository extends IRepository<Integer, User> {
    User findByUsername(String alias);
}
