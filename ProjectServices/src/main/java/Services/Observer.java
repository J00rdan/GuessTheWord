package Services;

import Model.Game;

public interface Observer {
    void gameFinished(Game game) throws ServiceException;
}
