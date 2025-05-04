import Domain.Book;
import Domain.BookCopy;
import Domain.Rent;
import Domain.User;
import Interfaces.IBookCopyRepository;
import Interfaces.IBookRepository;
import Interfaces.IRentRepository;
import Interfaces.IUserRepository;
import Service.AppException;
import Service.IObserver;
import Service.IService;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServicesImpl implements IService {

    private final IUserRepository userRepository;
    private final IBookRepository bookRepository;
    private final IBookCopyRepository bookCopyRepository;
    private final IRentRepository rentRepository;
    private final Map<Integer, IObserver> loggedClients;
    private final int defaultThreadsNo = 5;

    public ServicesImpl(IUserRepository userRepo, IBookRepository bookRepo, IBookCopyRepository bookCopyRepo, IRentRepository rentRepo) {
        this.userRepository = userRepo;
        this.bookRepository = bookRepo;
        this.bookCopyRepository = bookCopyRepo;
        this.rentRepository = rentRepo;
        this.loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized User login(User user, IObserver client) throws AppException {
        User userR = userRepository.findByUsername(user.getUsername());
        if (userR != null) {
            if (userR.getPassword().equals(user.getPassword())) {
                if (loggedClients.get(userR.getId()) != null)
                    throw new AppException("User already logged in.");
                loggedClients.put(userR.getId(), client);
                return userR;
            } else {
                throw new AppException("Wrong user or password.");
            }
        } else {
            throw new AppException("Authentication failed.");
        }
    }



    @Override
    public synchronized void logout(User user) throws AppException {
        User userR = userRepository.findByUsername(user.getUsername());
        IObserver removed = loggedClients.remove(userR.getId());
        if (removed == null)
            throw new AppException("User " + user.getId() + " is not logged in.");
    }

    @Override
    public synchronized Iterable<Integer> getAllBookCopies() throws AppException {
        return bookCopyRepository.getAll();
    }


    @Override
    public synchronized Collection<Rent> getAllRents() throws AppException {
        Collection<Rent> rents = (Collection<Rent>) rentRepository.getAll();
        if (rents == null) {
            return new ArrayList<>(); // return listă goală, nu null!
        }
        return rents;
    }

    @Override
    public synchronized int getRentCountForUser(User user) {
        int count = 0;
        for (Rent bookCopy : rentRepository.getAll()) {
            if (bookCopy.getId() == user.getId()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public synchronized void rentBook(User user, BookCopy bookCopy, LocalDate startDate, LocalDate endDate, String status) throws AppException {
        // 1. Creează închirierea
        Rent rent = new Rent(user, bookCopy, startDate, endDate, status);
        rentRepository.add(rent);

        // 2. Actualizează statusul în memorie
        bookCopy.setStatus(BookCopy.Status.BORROWED);

        // 3. Actualizează și în baza de date
        System.out.println("BEFORE UPDATE → BookCopy ID: " + bookCopy.getId());
        BookCopy before = bookCopyRepository.findOne(bookCopy.getId());
        System.out.println("Status in DB BEFORE update: " + before.getStatus());

        bookCopyRepository.update(bookCopy, bookCopy.getId());

        BookCopy after = bookCopyRepository.findOne(bookCopy.getId());
        System.out.println("AFTER UPDATE → BookCopy ID: " + after.getId() + ", Status in DB: " + after.getStatus());

        // 4. Notifică toți clienții conectați
        notifyClientsUpdate();
    }



    @Override
    public synchronized void addRent(Rent rent) throws AppException {
        rentRepository.add(rent);

        BookCopy copy = rent.getBookCopy();
        copy.setStatus(BookCopy.Status.BORROWED);
        bookCopyRepository.update(copy, copy.getId());
        System.out.println("=== ADD RENT ===");
        System.out.println("User: " + rent.getUser().getUsername());
        System.out.println("BookCopy ID: " + rent.getBookCopy().getId());
        System.out.println("Start date: " + rent.getStartDate());
        System.out.println("End date: " + rent.getEndDate());

        notifyClientsUpdate();
    }

    public BookCopy getBookCopyById(int id) throws AppException {
        BookCopy copy = bookCopyRepository.findOne(id);
        if (copy == null) return null;

        Book book = bookRepository.findOne(copy.getBook().getId());
        copy.setBook(book);
        return copy;
    }

    private void notifyClientsUpdate() throws AppException {
        List<BookCopy> availableCopies = bookCopyRepository.findByStatus("AVAILABLE");
        List<Integer> availableIds = availableCopies.stream()
                .map(BookCopy::getId)
                .toList();

        Collection<Rent> allRents = (Collection<Rent>) rentRepository.getAll();

        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);

        for (IObserver client : loggedClients.values()) {
            if (client != null) {
                executor.execute(() -> {
                    try {
                        System.out.println("→ Notifying client: updateBookCopies + updateRents");
                        client.updateBookCopies(availableIds);
                        client.updateRents(allRents);
                    } catch (AppException e) {
                        System.err.println("Error notifying client updates: " + e.getMessage());
                    }
                });
            }
        }

        executor.shutdown();
    }






}
