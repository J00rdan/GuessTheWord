package Persistence;

import Model.Game;

public interface GameRepository extends Repository<Integer, Game> {
    public Game saveGame(Game entity);

    public Iterable<Game> getAllFinished();
}
