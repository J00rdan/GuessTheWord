package Server;

import Model.Conf;
import Model.Game;
import Model.GameDto;
import Model.User;
import Persistence.ConfRepository;
import Persistence.GameDtoRepository;
import Persistence.GameRepository;
import Persistence.UserRepository;
import Services.Observer;
import Services.Service;
import Services.ServiceException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.sqrt;

public class ServerService implements Service {
    private Map<Integer, Observer> loggedClients;

    private UserRepository userRepository;
    private ConfRepository confRepository;
    private GameRepository gameRepository;
    private GameDtoRepository gameDtoRepository;

    public ServerService(UserRepository userRepository, ConfRepository confRepository, GameRepository gameRepository, GameDtoRepository gameDtoRepository) {
        this.userRepository = userRepository;
        this.confRepository = confRepository;
        this.gameRepository = gameRepository;
        this.gameDtoRepository = gameDtoRepository;
        loggedClients=new ConcurrentHashMap<>();
    }

    @Override
    public User login(User user, Observer client) throws ServiceException {
        if(!user.getUsername().equals("")){
            User userDb = userRepository.auth(user.getUsername());
            if(userDb != null){
                loggedClients.put(userDb.getId(), client);
                return userDb;
            }
            throw new ServiceException("Authentication failed.");
        }
        return null;
    }

    @Override
    public Map<String, String> startGame(User user) throws ServiceException {
        Random r = new Random();
        int low = 1;
        int high = 5;
        int confId = r.nextInt(high-low) + low;;

        Game game = new Game(user.getUsername(), 0, confId, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        Map<String, String> map = new HashMap<>();
        map.put("gameId", String.valueOf(gameRepository.saveGame(game).getId()));
        map.put("letters", confRepository.findOne(confId).getMask());

        return map;
    }

    @Override
    public int continueGame(Map<String, String> map) throws ServiceException {
        int found = 0;

        Game game = gameRepository.findOne(Integer.valueOf(map.get("gameId")));
        Conf conf = confRepository.findOne(game.getConfId());

        String letter = map.get("input");

        int points = -2;

        String testString = conf.getWord();

        if(conf.getWord().contains(letter)){
            points = testString.length() - testString.replace(letter, "").length();
            game.setGuessedLetters(game.getGuessedLetters() + letter);
            game.setTotalGuessedLetters(game.getTotalGuessedLetters() + points);
            found = 1;
        }

//        Random r = new Random();
//        int low = 1;
//        int high = 11;
//        int points = r.nextInt(high-low) + low;

        System.out.println(points);
        game.setTotalPoints(game.getTotalPoints() + points);
        game.setTries(game.getTries() + 1);

        if(game.getTotalGuessedLetters() == conf.getWord().length())
            game.setTries(4);

        gameRepository.update(game);

        if(game.getTries() == 4)
            return 0;
        else if(found == 1) return 1;
        return 2;
    }

    @Override
    public Game endGame(int gameId) throws ServiceException {
        Game game = gameRepository.findOne(gameId);
        GameDto gameDto = new GameDto(game.getUsername(), game.getTotalPoints(), game.getGuessedLetters());
        gameDtoRepository.save(gameDto);
        notifyGameFinished(game);
        return game;
    }

    @Override
    public Conf getConfByID(int confId) throws ServiceException {
        return confRepository.findOne(confId);
    }

    @Override
    public Iterable<Game> getAllGamesFinished() throws ServiceException {
        return gameRepository.getAllFinished();
    }

    @Override
    public void logout(User user, Observer client) throws ServiceException {
        Observer localClient=loggedClients.remove(user.getId());
        if (localClient==null)
            throw new ServiceException("User "+user.getId()+" is not logged in.");
    }

    private final int defaultThreadsNo=5;
    private void notifyGameFinished(Game game) throws ServiceException {
        System.out.println(loggedClients);
        Iterable<Game> games = gameRepository.getAllFinished();

        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);

        for(Observer client: loggedClients.values()){
            executor.execute(() -> {
                try {
                    //System.out.println("Notifying [" + employee.getId()+ "]");
                    client.gameFinished(game);
                } catch (ServiceException e) {
                    System.err.println("Error notifying friend " + e);
                }
            });
        }

        executor.shutdown();
    }
}
