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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class AplicatieServicesRpcProxy implements IService {
    private String host;
    private int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public AplicatieServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }

    @Override
    public User login(User user, IObserver client) throws AppException {
        initializeConnection();  // Start connection + reader thread

        UserDTO userDTO = DTOUtils.getDTO(user);
        Request request = new Request.Builder()
                .type(RequestType.LOGIN)
                .data(userDTO)
                .build();

        sendRequest(request);
        Response response = readResponse();

        if (response.type() == ResponseType.USER_LOGGED_IN) {
            this.client = client;  // SETĂM DOAR DUPĂ ce s-a confirmat LOGIN
            UserDTO dto = (UserDTO) response.data();
            user.setId(dto.getId());
            user.setName(dto.getName());
            user.setPhone(dto.getPhone());
            user.setRole(dto.getRole());
            user.setUsername(dto.getUsername());
            return user;
        } else if (response.type() == ResponseType.ERROR) {
            closeConnection();
            throw new AppException(response.data().toString());
        }

        throw new AppException("Unexpected response: " + response.type());
    }






    public void logout(User user) throws AppException {
        UserDTO udto = DTOUtils.getDTO(user);
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(udto).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new AppException(err);
        }
    }


    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request) throws AppException {
        try {
            if (output == null) {
                initializeConnection();  //  important
            }
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new AppException("Error sending object " + e);
        }
    }


    private Response readResponse() {
        Response response = null;
        try {

            response = qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }


    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.BOOKS_UPDATE
                || response.type() == ResponseType.RENTS_UPDATE;
    }

    @Override
    public Iterable<Integer> getAllBookCopies() throws AppException {
        Request req = new Request.Builder()
                .type(RequestType.GET_ALL_BOOKS)
                .build();

        sendRequest(req);
        Response response = readResponse();

        if (response.type() == ResponseType.ERROR) {
            throw new AppException(response.data().toString());
        }

        List<BookCopyDTO> dtoList = (List<BookCopyDTO>) response.data();
        List<Integer> ids = dtoList.stream()
                .map(BookCopyDTO::getId)
                .toList();

        return ids;
    }


    @Override
    public Collection<Rent> getAllRents() throws AppException {
        Request req = new Request.Builder()
                .type(RequestType.GET_ALL_RENTS)
                .build();

        sendRequest(req);
        Response response = readResponse();

        if (response.type() == ResponseType.ERROR) {
            throw new AppException(response.data().toString());
        }

        List<RentDTO> dtoList = (List<RentDTO>) response.data();
        List<Rent> result = new ArrayList<>();
        for (RentDTO dto : dtoList) {
            result.add(DTOUtils.getFromRentDTO(dto));
        }

        return result;
    }


    @Override
    public int getRentCountForUser(User user) {
        try {
            UserDTO dto = DTOUtils.getDTO(user);
            Request req = new Request.Builder()
                    .type(RequestType.GET_RENT_COUNT_FOR_USER)
                    .data(dto)
                    .build();

            sendRequest(req);
            Response response = readResponse();

            if (response.type() == ResponseType.ERROR) {
                throw new AppException(response.data().toString());
            }

            return (Integer) response.data();
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void rentBook(User user, BookCopy copy, LocalDate startDate, LocalDate endDate, String status) throws AppException {
        Rent rent = new Rent(user, copy, startDate, endDate, status);
        RentDTO dto = DTOUtils.getDTO(rent);

        Request req = new Request.Builder()
                .type(RequestType.ADD_RENT)
                .data(dto)
                .build();

        sendRequest(req);
        Response response = readResponse();

        if (response.type() == ResponseType.ERROR) {
            throw new AppException(response.data().toString());
        }
    }

    @Override
    public void addRent(Rent rent) throws AppException {
        RentDTO dto = DTOUtils.getDTO(rent);

        Request req = new Request.Builder()
                .type(RequestType.ADD_RENT)
                .data(dto)
                .build();

        sendRequest(req);
        Response response = readResponse();

        if (response.type() == ResponseType.ERROR) {
            throw new AppException(response.data().toString());
        }
    }

    @Override
    public BookCopy getBookCopyById(int id) throws AppException {
        Request req = new Request.Builder()
                .type(RequestType.GET_BOOK_COPY_BY_ID)
                .data(id)
                .build();

        sendRequest(req);
        Response response = readResponse();

        if (response.type() == ResponseType.ERROR) {
            throw new AppException(response.data().toString());
        }

        BookCopyDTO dto = (BookCopyDTO) response.data();
        return DTOUtils.getFromBookCopyDTO(dto);
    }

    @Override
    public void returnBook(int rentId) throws AppException {
        Request req = new Request.Builder()
                .type(RequestType.RETURN_BOOK)
                .data(rentId)
                .build();

        sendRequest(req);
        Response response = readResponse();

        if (response.type() == ResponseType.ERROR) {
            throw new AppException(response.data().toString());
        }
    }

    private void handleUpdate(Response response) throws AppException {
        if (client == null) {
            System.out.println("Ignoring update because client (observer) is not yet set.");
            return;
        }

        switch (response.type()) {
            case BOOKS_UPDATE -> {
                Collection<BookCopyDTO> bookCopyDTOs = (Collection<BookCopyDTO>) response.data();
                List<Integer> ids = bookCopyDTOs.stream()
                        .map(BookCopyDTO::getId)
                        .toList();

                System.out.println("Proxy received BOOKS_UPDATE with " + ids.size() + " DTOs");
                client.updateBookCopies(ids);
            }

            case RENTS_UPDATE -> {
                Collection<RentDTO> rentDTOs = (Collection<RentDTO>) response.data();
                Collection<Rent> rents = rentDTOs.stream()
                        .map(DTOUtils::getFromRentDTO)
                        .toList();

                System.out.println("Proxy received RENTS_UPDATE with " + rents.size() + " entries");
                client.updateRents(rents);
            }

            default -> System.out.println("Unhandled update type: " + response.type());
        }
    }








    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("response received: " + response);

                    if (isUpdate((Response) response)) {
                        try {
                            handleUpdate((Response) response);
                        } catch (Exception e) {
                            System.err.println("Exception in handleUpdate: " + e.getMessage());
                        }
                    } else {
                        qresponses.put((Response) response);
                    }

                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error: " + e.getMessage());
                    break;  // închidem bucla dacă s-a pierdut conexiunea
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
