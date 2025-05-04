

import Interfaces.IBookCopyRepository;
import Interfaces.IBookRepository;
import Interfaces.IRentRepository;
import Interfaces.IUserRepository;
import Repository.*;
import Service.IService;
import utils.AbstractServer;
import utils.ChatRpcConcurrentServer;

import java.io.IOException;
import utils.ServerException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort=55555;
    public static void main(String[] args) {
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/aplicatieserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
            return;
        }
        IUserRepository userRepository = new UserRepository(serverProps);
        IBookRepository bookRepository = new BookRepository(serverProps);
        IBookCopyRepository bookCopyRepository = new BookCopyRepository(serverProps);
        IRentRepository rentRepository = new RentRepository(serverProps);



        IService chatServerImpl=new ServicesImpl(userRepository, bookRepository, bookCopyRepository, rentRepository);
        int chatServerPort=defaultPort;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+chatServerPort);
        AbstractServer server = new ChatRpcConcurrentServer(chatServerPort, chatServerImpl);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                System.err.println("Error stopping server "+e.getMessage());
            }
        }
    }
}
