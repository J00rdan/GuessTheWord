package Persistence;

import Model.GameDto;

public interface GameDtoRepository extends Repository<Integer, GameDto>{
    Iterable<GameDto> findByUsername(String username);
}
