package Persistence;

import Model.Conf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class ConfDBRepository implements ConfRepository{

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    @Autowired
    public ConfDBRepository(Properties props) {
        logger.info("Initializing ConfDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public Conf findOne(Integer id) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        String query = "select * from confs where id = (?)";
        try(PreparedStatement preStmt = con.prepareStatement(query)){
            preStmt.setInt(1, id);
            try(ResultSet result = preStmt.executeQuery()){
                while(result.next()){

                    String l = result.getString("letters");

                    String w1 = result.getString("w1");
                    String w2 = result.getString("w2");
                    String w3 = result.getString("w3");
                    Conf conf = new Conf(l, w1, w2, w3);
                    conf.setId(id);

                    logger.traceExit();
                    return conf;
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Iterable<Conf> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Conf> confs = new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from confs")){
            try(ResultSet result = preStmt.executeQuery()){
                while(result.next()){
                    int id = result.getInt("id");
                    String l = result.getString("letters");

                    String w1 = result.getString("w1");
                    String w2 = result.getString("w2");
                    String w3 = result.getString("w3");
                    Conf conf = new Conf(l, w1, w2, w3);
                    conf.setId(id);

                    confs.add(conf);
                }
            }
        }
        catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB "+ e);
        }
        logger.traceExit();
        return confs;
    }

    @Override
    public void save(Conf entity) {
        logger.traceEntry("saving task {} ", entity);
        Connection con = dbUtils.getConnection();

        String query = "insert into confs (letters, w1, w2, w3) values (?, ?, ?, ?)";
        try(PreparedStatement preStmt = con.prepareStatement(query)){

            preStmt.setString(1, entity.getLetters());
            preStmt.setString(2, entity.getW1());
            preStmt.setString(3, entity.getW2());
            preStmt.setString(4, entity.getW3());
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
    public void update(Conf entity) {

    }

    @Override
    public Conf saveConf(Conf entity) {
        logger.traceEntry("saving task {} ", entity);
        Connection con = dbUtils.getConnection();

        String query = "insert into confs (letters, w1, w2, w3) values (?, ?, ?, ?)";
        try(PreparedStatement preStmt = con.prepareStatement(query)){

            preStmt.setString(1, entity.getLetters());
            preStmt.setString(2, entity.getW1());
            preStmt.setString(3, entity.getW2());
            preStmt.setString(4, entity.getW3());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);

            entity.setId(selectMaxId());
        }catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return entity;
    }

    private int selectMaxId(){
        Connection con = dbUtils.getConnection();
        int id = 0;

        try (PreparedStatement preparedStatement = con.prepareStatement("select MAX(id) as id from confs")) {

            ResultSet result = preparedStatement.executeQuery();
            result.next();
            id = result.getInt("id");
        } catch (SQLException exception) {
            System.err.println("Error DB" + exception);
        }
        return id;
    }
}
