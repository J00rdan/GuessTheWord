package Persistence;

import Model.User;

public interface UserRepository extends Repository<Integer, User>{
    User auth(String username);
}