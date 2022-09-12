package Persistence;

import Model.Game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GameDBRepository implements GameRepository{

    private static final Logger logger = LogManager.getLogger();

    private SessionFactory sessionFactory;

    public GameDBRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Game findOne(Integer id) {
        logger.traceEntry();

        try {
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    String queryString = "from Game game where game.id = :idP";

                    List<Game> result = session.createQuery(queryString, Game.class).setParameter("idP", id).list();
                    tx.commit();

                    if (result.size() == 1) {
                        return result.get(0);
                    }

                } catch (RuntimeException ex) {
                    System.err.println("Eroare la findOne " + ex);
                    if (tx != null)
                        tx.rollback();
                }
            }

        } catch (Exception e) {
            System.err.println("Exception " + e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Game> findAll() {
        logger.traceEntry();

        try {
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    List<Game> result = session.createQuery("from Game ", Game.class).list();
                    tx.commit();

                    return result;

                } catch (RuntimeException ex) {
                    System.err.println("Eroare la findAll " + ex);
                    if (tx != null)
                        tx.rollback();
                }
            }

        } catch (Exception e) {
            System.err.println("Exception " + e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save(Game entity) {
        logger.traceEntry();

        try {
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    session.save(entity);

                    tx.commit();

                } catch (RuntimeException ex) {
                    System.err.println("Eroare la save " + ex);
                    if (tx != null)
                        tx.rollback();
                }
            }

        } catch (Exception e) {
            System.err.println("Exception " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        logger.traceEntry();

        try {
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    String queryString = "from Game game where game.id = :idP";

                    Game game = session.createQuery(queryString, Game.class).setParameter("idP", id).setMaxResults(1).uniqueResult();

                    session.delete(game);

                    tx.commit();

                } catch (RuntimeException ex) {
                    System.err.println("Eroare la delete " + ex);
                    if (tx != null)
                        tx.rollback();
                }
            }

        } catch (Exception e) {
            System.err.println("Exception " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void update(Game entity) {
        logger.traceEntry();

        try {
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    Game game = session.load(Game.class, entity.getId());
                    game.setTotalPoints(entity.getTotalPoints());
                    game.setTries(entity.getTries());
                    game.setGuessedLetters(entity.getGuessedLetters());
                    game.setTotalGuessedLetters(entity.getTotalGuessedLetters());

                    tx.commit();

                } catch (RuntimeException ex) {
                    System.err.println("Eroare la update " + ex);
                    if (tx != null)
                        tx.rollback();
                }
            }

        } catch (Exception e) {
            System.err.println("Exception " + e);
            e.printStackTrace();
        }
    }

    @Override
    public Game saveGame(Game entity) {
        logger.traceEntry();

        try {
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    session.save(entity);

                    tx.commit();

                    return entity;

                } catch (RuntimeException ex) {
                    System.err.println("Eroare la save " + ex);
                    if (tx != null)
                        tx.rollback();
                }
            }

        } catch (Exception e) {
            System.err.println("Exception " + e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Game> getAllFinished() {
        logger.traceEntry();

        try {
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    List<Game> result = session.createQuery("from Game game where game.tries = 4 ORDER BY game.totalPoints desc ", Game.class).list();
                    tx.commit();

                    return result;

                } catch (RuntimeException ex) {
                    System.err.println("Eroare la findAll " + ex);
                    if (tx != null)
                        tx.rollback();
                }
            }

        } catch (Exception e) {
            System.err.println("Exception " + e);
            e.printStackTrace();
        }
        return null;
    }

//    private int selectMaxId(){
//        Connection con = dbUtils.getConnection();
//        int id = 0;
//
//        try (PreparedStatement preparedStatement = con.prepareStatement("select MAX(id) as id from matches")) {
//
//            ResultSet result = preparedStatement.executeQuery();
//            result.next();
//            id = result.getInt("id");
//        } catch (SQLException exception) {
//            System.err.println("Error DB" + exception);
//        }
//        return id;
//    }
}
