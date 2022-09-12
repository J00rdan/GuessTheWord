package rpcprotocol;

import Model.Game;
import Model.User;
import Services.Observer;
import Services.Service;
import Services.ServiceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

public class ClientRpcWorker implements Runnable, Observer {
    private Service service;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ClientRpcWorker(Service service, Socket connection) {
        this.service = service;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void gameFinished(Game game) throws ServiceException {
        Response resp=new Response.Builder().type(ResponseType.GAME_FINISHED).data(game).build();
        System.out.println("Game finished");
        try {
            sendResponse(resp);
        } catch (IOException e) {
            throw new ServiceException("Sending error: "+e);
        }
    }

//    @Override
//    public void ticketsSold(Customer customer) throws ServiceException {
//        Response resp=new Response.Builder().type(ResponseType.TICKETS_SOLD).data(customer).build();
//        System.out.println("Sold tickets");
//        try {
//            sendResponse(resp);
//        } catch (IOException e) {
//            throw new ServiceException("Sending error: "+e);
//        }
//    }

    @Override
    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }

    private static Response okResponse=new Response.Builder().type(ResponseType.OK).build();
    //  private static Response errorResponse=new Response.Builder().type(ResponseType.ERROR).build();
    private Response handleRequest(Request request){
        Response response=null;
        String handlerName="handle"+(request).type();
        System.out.println("HandlerName "+handlerName);
        try {
            Method method=this.getClass().getDeclaredMethod(handlerName, Request.class);
            response=(Response)method.invoke(this,request);
            System.out.println("Method "+handlerName+ " invoked");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        synchronized(output){
            output.writeObject(response);
            output.flush();
        }
    }
    private Response handleLOGIN(Request request){
        System.out.println("Login request ..."+request.type());
        User user=(User) request.data();

        try {
            User userDb = service.login(user, this);
            if(userDb != null)
                return new Response.Builder().type(ResponseType.OK).data(userDb).build();
            return new Response.Builder().type(ResponseType.ERROR).data("Wrong").build();
        } catch (ServiceException e) {
            connected=false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleSTART_GAME(Request request){
        System.out.println("START_GAME request ..."+request.type());
        User user=(User) request.data();

        try {
            return new Response.Builder().type(ResponseType.OK).data(service.startGame(user)).build();
        } catch (ServiceException e) {
            connected=false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleCONTINUE_GAME(Request request){
        System.out.println("CONTINUE_GAME request ..."+request.type());
        Map<String, String> map = (Map<String, String>) request.data();

        try {
            return new Response.Builder().type(ResponseType.OK).data(service.continueGame(map)).build();
        } catch (ServiceException e) {
            connected=false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleEND_GAME(Request request){
        System.out.println("GAME_ENDED request ..."+request.type());
        int gameId = (int) request.data();

        try {
            return new Response.Builder().type(ResponseType.OK).data(service.endGame(gameId)).build();
        } catch (ServiceException e) {
            connected=false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleGET_CONF(Request request){
        System.out.println("GET_CONF request ..."+request.type());
        int confId = (int) request.data();

        try {
            return new Response.Builder().type(ResponseType.OK).data(service.getConfByID(confId)).build();
        } catch (ServiceException e) {
            connected=false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleGET_GAMES(Request request){
        System.out.println("GET_GAMES request ..."+request.type());

        try {
            return new Response.Builder().type(ResponseType.OK).data(service.getAllGamesFinished()).build();
        } catch (ServiceException e) {
            connected=false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
    private Response handleLOGOUT(Request request){
        System.out.println("Logout request...");
        User user=(User) request.data();
        try {
            service.logout(user, this);
            connected=false;
            return okResponse;

        } catch (ServiceException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }
}
