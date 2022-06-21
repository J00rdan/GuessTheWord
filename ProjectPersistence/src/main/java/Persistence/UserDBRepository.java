package Persistence;

import Model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserDBRepository implements UserRepository{

    private JdbcUtils dbUtils;

    private static final Logger logger= LogManager.getLogger();

    public UserDBRepository(Properties props) {
        logger.info("Initializing UserDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }


    @Override
    public User findOne(Integer id) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        String query = "select * from users where id = (?)";
        try(PreparedStatement preStmt = con.prepareStatement(query)){
            preStmt.setInt(1, id);
            try(ResultSet result = preStmt.executeQuery()){
                while(result.next()){

                    String username = result.getString("user_name");
                    User user = new User(username);
                    user.setId(id);

                    logger.traceExit();
                    return user;
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
    public Iterable<User> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<User> users = new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from users")){
            try(ResultSet result = preStmt.executeQuery()){
                while(result.next()){
                    int id = result.getInt("id");
                    String username = result.getString("user_name");
                    User user = new User(username);
                    user.setId(id);

                    users.add(user);
                }
            }
        }
        catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB "+ e);
        }
        logger.traceExit();
        return users;
    }

    @Override
    public void save(User entity) {
        logger.traceEntry("saving task {} ", entity);
        Connection con = dbUtils.getConnection();

        String query = "insert into users (user_name) values (?)";
        try(PreparedStatement preStmt = con.prepareStatement(query)){

            preStmt.setString(1, entity.getUsername());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);

        }catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Integer id) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        String query = "delete from users where id = (?)";
        try(PreparedStatement preStmt = con.prepareStatement(query)){
            preStmt.setInt(1, id);

            int result = preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);

        }catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void update(User entity) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        String query = "update users set user_name = (?) where id = (?)";
        try(PreparedStatement preStmt = con.prepareStatement(query)){
            preStmt.setString(1, entity.getUsername());
            preStmt.setInt(2, entity.getId());

            int result = preStmt.executeUpdate();
            logger.trace("Updated {} instances", result);

        }catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public User auth(String username) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();

        String query = "select * from users where user_name = (?)";
        try(PreparedStatement preStmt = con.prepareStatement(query)){
            preStmt.setString(1, username);
            try(ResultSet result = preStmt.executeQuery()){
                while(result.next()){

                    int id = result.getInt("id");
                    User user = new User(username);
                    user.setId(id);

                    logger.traceExit();
                    return user;
                }
            }
        }catch (SQLException ex){
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return null;

//        logger.traceEntry();
//        Connection con = dbUtils.getConnection();
//
//        String query = "select * from organisers where id = (?)";
//        try(PreparedStatement preStmt = con.prepareStatement(query)){
//            preStmt.setInt(1, id);
//            try(ResultSet result = preStmt.executeQuery()){
//                while(result.next()){
//
//                    String pass = result.getString("pass");
//
//                    if(pass.equals(password)){
//                        int checkPoint = result.getInt("check_point");
//
//                        Organiser organiser = new Organiser(password, checkPoint);
//                        organiser.setId(id);
//
//                        logger.traceExit();
//                        return organiser;
//                    }
//                    else return null;
//                }
//            }
//        }catch (SQLException ex){
//            logger.error(ex);
//            System.err.println("Error DB " + ex);
//        }
//        logger.traceExit();
//        return null;
    }
}
