package Services;

import Model.Conf;
import Model.Game;
import Model.User;

import java.util.Map;

public interface Service {
    public User login(User user, Observer client) throws ServiceException;

    public Map<String, String> startGame(User user) throws ServiceException;

    public int continueGame(Map<String, String> map) throws ServiceException;

    public Game endGame(int gameId) throws ServiceException;

    public Conf getConfByID(int confId) throws ServiceException;

    public Iterable<Game> getAllGamesFinished() throws ServiceException;

    public void logout(User user, Observer client) throws ServiceException;
}
