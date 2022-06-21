package Services;

import Model.User;

import java.util.Map;

public interface Service {
    public User login(User user, Observer client) throws ServiceException;

    public void logout(User user, Observer client) throws ServiceException;
}
