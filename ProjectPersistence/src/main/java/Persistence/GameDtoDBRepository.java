package Persistence;

import Model.Conf;
import Model.GameDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class GameDtoDBRepository implements GameDtoRepository{
    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    @Autowired
    public GameDtoDBRepository(Properties props) {
        logger.info("Initializing GameDtoDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public Iterable<GameDto> findByUsername(String username) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<GameDto> gameDtos = new ArrayList<>();
        String query = "select * from gamesdto where username = (?)";
        try(PreparedStatement preStmt = con.prepareStatement(query)){
            preStmt.setString(1, username);
            try(ResultSet result = preStmt.executeQuery()){
                while(result.next()){

                    int id = result.getInt("id");
                    int totalPoints = result.getInt("total_points");
                    String guessedLetters = result.getString("guessed_letters");
                    GameDto gameDto = new GameDto(username, totalPoints, guessedLetters);
                    gameDto.setId(id);

                    gameDtos.add(gameDto);
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return gameDtos;
    }

    @Override
    public GameDto findOne(Integer integer) {
        return null;
    }

    @Override
    public Iterable<GameDto> findAll() {
        return null;
    }

    @Override
    public void save(GameDto entity) {
        logger.traceEntry("saving task {} ", entity);
        Connection con = dbUtils.getConnection();

        String query = "insert into gamesdto (username, total_points, guessed_letters) values (?, ?, ?)";
        try(PreparedStatement preStmt = con.prepareStatement(query)){

            preStmt.setString(1, entity.getUsername());
            preStmt.setInt(2, entity.getTotalPoints());
            preStmt.setString(3, entity.getGuessedLetters());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);

        }catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Integer integer) {

    }

    @Override
    public void update(GameDto entity) {

    }
}
