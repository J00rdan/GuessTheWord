package rpcprotocol;

import Model.Conf;
import Model.Game;
import Model.User;
import Services.Observer;
import Services.Service;
import Services.ServiceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesRpcProxy implements Service {
    private String host;
    private int port;

    private Observer client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
    }

    private void initializeConnection() throws ServiceException {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request)throws ServiceException {
        try {
            synchronized(output){
                output.writeObject(request);
                output.flush();
            }
        } catch (IOException e) {
            throw new ServiceException("Error sending object "+e);
        }

    }

    private Response readResponse() throws ServiceException {
        Response response=null;
        try{

            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }

    @Override
    public User login(User user, Observer client) throws ServiceException {
        initializeConnection();
        Request req=new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            this.client=client;
            return (User) response.data();
        }
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            throw new ServiceException(err);
        }
        return null;
    }

    @Override
    public Map<String, String> startGame(User user) throws ServiceException {
        Request req=new Request.Builder().type(RequestType.START_GAME).data(user).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServiceException(err);
        }
        return (Map<String, String>) response.data();
    }

    @Override
    public int continueGame(Map<String, String> map) throws ServiceException {
        Request req=new Request.Builder().type(RequestType.CONTINUE_GAME).data(map).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServiceException(err);
        }
        return (int) response.data();
    }

    @Override
    public Game endGame(int gameId) throws ServiceException {
        Request req=new Request.Builder().type(RequestType.END_GAME).data(gameId).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServiceException(err);
        }
        return (Game) response.data();
    }

    @Override
    public Conf getConfByID(int confId) throws ServiceException {
        Request req=new Request.Builder().type(RequestType.GET_CONF).data(confId).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServiceException(err);
        }
        return (Conf) response.data();
    }

    @Override
    public Iterable<Game> getAllGamesFinished() throws ServiceException {
        Request req=new Request.Builder().type(RequestType.GET_GAMES).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServiceException(err);
        }
        return (Iterable<Game>) response.data();
    }

    @Override
    public void logout(User user, Observer client) throws ServiceException {
        Request req=new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(req);
        Response response=readResponse();
        closeConnection();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new ServiceException(err);
        }
    }

    private void handleUpdate(Response response){

        if (response.type()== ResponseType.GAME_FINISHED){
            Game game = (Game) response.data();
            try {
                client.gameFinished(game);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isUpdate(Response response){
        return response.type()== ResponseType.GAME_FINISHED;
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{

                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
