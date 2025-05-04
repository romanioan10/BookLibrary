package rpcprotocol;

import DTOs.*;
import Domain.BookCopy;
import Domain.Rent;
import Domain.User;
import Service.AppException;
import Service.IObserver;
import Service.IService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class AplicatieClientRpcWorker implements Runnable, IObserver {
    private IService server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    public AplicatieClientRpcWorker(IService server, Socket connection) {
        this.server = server;
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

    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException | AppException | ClassNotFoundException e) {
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

    private Response handleRequest(Request request) throws AppException {
        Response response=null;
        if (request.type() == RequestType.LOGIN) {
            System.out.println("Login request received...");
            UserDTO userDTO = (UserDTO) request.data();
            User user = DTOUtils.getFromUserDTO(userDTO);

            try {
                // Login pe server – returnează User complet cu ID
                User userR = server.login(user, this);

                // Convertim User complet în UserDTO
                UserDTO responseDTO = DTOUtils.getDTO(userR);

                // Trimitem UserDTO înapoi către client
                return new Response.Builder()
                        .type(ResponseType.USER_LOGGED_IN)
                        .data(responseDTO)
                        .build();

            } catch (AppException e) {
                connected = false;
                return new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        }



        if (request.type()== RequestType.LOGOUT){
            System.out.println("Logout request");
            UserDTO udto=(UserDTO) request.data();
            User user= DTOUtils.getFromUserDTO(udto);
            try {
                server.logout(user);
                connected=false;
                return okResponse;

            } catch (AppException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.GET_ALL_RENTS) {
            System.out.println("GetAllRents Request ...");
            try {
                List<Rent> rents = (List<Rent>) server.getAllRents(); // returns List<Rent>
                List<RentDTO> rentDTOs = rents.stream()
                        .map(DTOUtils::getDTO)
                        .collect(Collectors.toList());

                return new Response.Builder()
                        .type(ResponseType.SEND_ALL_RENTS)
                        .data(rentDTOs)
                        .build();

            } catch (AppException e) {
                return new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        }


        if (request.type() == RequestType.GET_ALL_BOOKS) {
            System.out.println("GetAllBooks Request ...");
            try {
                Iterable<Integer> bookCopyIds = server.getAllBookCopies(); // returnează ID-uri
                List<BookCopyDTO> bookCopyDTOs = new ArrayList<>();

                for (Integer id : bookCopyIds) {
                    BookCopy copy = server.getBookCopyById(id);
                    if (copy != null) {
                        bookCopyDTOs.add(DTOUtils.getDTO(copy));
                    }
                }

                return new Response.Builder()
                        .type(ResponseType.SEND_ALL_BOOKS)
                        .data(bookCopyDTOs)
                        .build();

            } catch (AppException e) {
                return new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        }


        if (request.type() == RequestType.ADD_RENT) {
            System.out.println("AddRent Request ...");
            RentDTO rentDTO = (RentDTO) request.data();
            Rent rent = DTOUtils.getFromRentDTO(rentDTO);
            try {
                server.addRent(rent);

                return new Response.Builder()
                        .type(ResponseType.OK)
                        .data(null)
                        .build(); // răspuns explicit

            } catch (AppException e) {
                return new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        }

        if (request.type() == RequestType.GET_BOOK_COPY_BY_ID) {
            try {
                int id = (Integer) request.data();
                BookCopy copy = server.getBookCopyById(id);
                BookCopyDTO dto = DTOUtils.getDTO(copy);

                return new Response.Builder()
                        .type(ResponseType.SEND_BOOK_COPY_BY_ID)
                        .data(dto)
                        .build();
            } catch (AppException e) {
                return new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }


        }
        return response;
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }

    @Override
    public void updateBookCopies(Iterable<Integer> bookCopyIds) throws AppException {
        List<BookCopyDTO> bookCopyDTOs = new ArrayList<>();

        for (Integer id : bookCopyIds) {
            BookCopy copy = server.getBookCopyById(id);
            if (copy != null) {
                bookCopyDTOs.add(DTOUtils.getDTO(copy));
            }
        }

        Response response = new Response.Builder()
                .type(ResponseType.BOOKS_UPDATE)
                .data(bookCopyDTOs)
                .build();

        try {
            sendResponse(response);
        } catch (IOException e) {
            System.err.println("Error sending BOOKS_UPDATE: " + e.getMessage());
        }
    }



    @Override
    public void updateRents(Collection<Rent> rents) throws AppException {
        Collection<RentDTO> rentDTOs = rents.stream()
                .map(DTOUtils::getDTO)
                .toList();

        Response response = new Response.Builder()
                .type(ResponseType.RENTS_UPDATE)
                .data(rentDTOs)
                .build();

        try {
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
