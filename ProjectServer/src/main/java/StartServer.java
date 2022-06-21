import Persistence.ConfDBRepository;
import Persistence.UserDBRepository;
import Server.ServerService;
import Services.Service;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import utils.AbstractServer;
import utils.ConcurrentServer;
import utils.ServerException;

import java.io.IOException;
import java.util.Properties;

public class StartServer {
    private static int defaultPort=55555;

//    static SessionFactory sessionFactory;
//
//    static void initialize() {
//        // A SessionFactory is set up once for an application!
//        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
//                .configure() // configures settings from hibernate.cfg.xml
//                .build();
//        try {
//            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
//        }
//        catch (Exception e) {
//            System.err.println("Exception here "+e);
//            StandardServiceRegistryBuilder.destroy( registry );
//        }
//    }
//
//    static void close(){
//        if ( sessionFactory != null ) {
//            sessionFactory.close();
//        }
//    }

    public static void main(String[] args) {
//        initialize();

        Properties serverProps=new Properties();
        try {
            serverProps.load(StartServer.class.getResourceAsStream("/server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find server.properties "+e);
            return;
        }

        UserDBRepository userDBRepository = new UserDBRepository(serverProps);
        ConfDBRepository confDBRepository = new ConfDBRepository(serverProps);

        Service srv = new ServerService(userDBRepository, confDBRepository);


        int chatServerPort=defaultPort;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+chatServerPort);
        AbstractServer server = new ConcurrentServer(chatServerPort, srv);
        try {
            server.start();

        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
//                close();
            }catch(ServerException e){
                System.err.println("Error stopping server "+e.getMessage());
            }
        }
    }
}
