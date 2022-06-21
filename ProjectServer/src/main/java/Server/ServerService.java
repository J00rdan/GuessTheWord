package Server;

import Model.Conf;
import Model.User;
import Persistence.ConfRepository;
import Persistence.UserRepository;
import Services.Observer;
import Services.Service;
import Services.ServiceException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.sqrt;

public class ServerService implements Service {
    private Map<Integer, Observer> loggedClients;

    private UserRepository userRepository;
    private ConfRepository confRepository;

    public ServerService(UserRepository userRepository, ConfRepository confRepository) {
        this.userRepository = userRepository;
        this.confRepository = confRepository;
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
    public void logout(User user, Observer client) throws ServiceException {
        Observer localClient=loggedClients.remove(user.getId());
        if (localClient==null)
            throw new ServiceException("User "+user.getId()+" is not logged in.");
    }

//    private final int defaultThreadsNo=5;
//    private void notifyScoutChecked(Scout scout) throws ServiceException {
//        System.out.println(loggedClients);
//        Iterable<Organiser> organisers = organiserRepository.findAll();
//
//        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
//
//        for(Observer client: loggedClients.values()){
//            executor.execute(() -> {
//                try {
//                    //System.out.println("Notifying [" + employee.getId()+ "]");
//                    client.scoutChecked(scout);
//                } catch (ServiceException e) {
//                    System.err.println("Error notifying friend " + e);
//                }
//            });
//        }
//
//        executor.shutdown();
//    }
}
