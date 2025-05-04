package DTOs;

import Domain.Book;
import Domain.BookCopy;
import Domain.Rent;
import Domain.User;

import java.time.LocalDate;

public class DTOUtils {

    public static UserDTO getDTO(User user)
    {   Integer id = user.getId();
        String username = user.getUsername();
        String password = user.getPassword();
        String name = user.getName();
        String phone = user.getPhone();
        int role = user.getRole();
        return new UserDTO(id, username, password , name, phone, role);
    }

    public static User getFromUserDTO(UserDTO userDTO){
        int id = userDTO.id;
        String username = userDTO.username;
        String password = userDTO.password;
        String name = userDTO.name;
        String phone = userDTO.phone;
        int role = userDTO.role;
        User user = new User(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setPhone(phone);
        user.setRole(role);
        return user;
    }

    public static BookDTO getDTO(Book book) {
        int id = book.getId();
        String title = book.getTitle();
        String author = book.getAuthor();
        return new BookDTO(id, title, author);
    }

    public static Book getFromBookDTO(BookDTO bookDTO) {
        int id = bookDTO.getId();
        String title = bookDTO.getTitle();
        String author = bookDTO.getAuthor();
        Book book = new Book(id, title, author);
        return book;
    }

    public static BookCopyDTO getDTO(BookCopy bookCopy) {
        if (bookCopy == null) {
            throw new IllegalArgumentException("bookCopy is null");
        }

        BookDTO bookDTO = getDTO(bookCopy.getBook()); // folosim conversie corectă

        return new BookCopyDTO(bookCopy.getId(), bookDTO, bookCopy.getStatus().toString());
    }




    public static BookCopy getFromBookCopyDTO(BookCopyDTO bookCopydto) {
        int id = bookCopydto.id;
        Book book = getFromBookDTO(bookCopydto.book); // conversie DTO → Book
        String status = bookCopydto.status;

        BookCopy bookCopy = new BookCopy(book);
        bookCopy.setId(id);
        bookCopy.setStatus(BookCopy.Status.valueOf(status));
        return bookCopy;
    }


    public static RentDTO getDTO(Rent rent) {
        int id = rent.getId();
        UserDTO userDTO = getDTO(rent.getUser());
        BookCopyDTO bookCopyDTO = getDTO(rent.getBookCopy());
        LocalDate startDate = rent.getStartDate();
        LocalDate endDate = rent.getEndDate();
        String status = rent.getStatus();
        return new RentDTO(id, userDTO, bookCopyDTO, startDate, endDate, status);
    }

    public static Rent getFromRentDTO(RentDTO dto) {
        int id = dto.getId();
        User user = getFromUserDTO(dto.getUserDTO());
        BookCopy bookCopy = getFromBookCopyDTO(dto.getBookCopyDTO());
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        String status = dto.getStatus();
        Rent rent = new Rent(user, bookCopy, startDate, endDate, status);
        rent.setId(id);
        return rent;
    }




//    public static UtilizatorDTO[] getDTO(Utilizator[] utilizatori) {
//        UtilizatorDTO[] utilizatoriDTO = new UtilizatorDTO[utilizatori.length];
//        for (int i = 0; i < utilizatori.length; i++) {
//            utilizatoriDTO[i] = getDTO(utilizatori[i]);
//        }
//        return utilizatoriDTO;
//    }
//
//    public static Utilizator[] getFromUtilizatorDTO(UtilizatorDTO[] utilizatoriDTO) {
//        Utilizator[] utilizatori = new Utilizator[utilizatoriDTO.length];
//        for (int i = 0; i < utilizatoriDTO.length; i++) {
//            utilizatori[i] = getFromUtilizatorDTO(utilizatoriDTO[i]);
//        }
//        return utilizatori;
//    }
//
//    public static ClientDTO[] getDTO(Client[] clienti) {
//        ClientDTO[] clientiDTO = new ClientDTO[clienti.length];
//        for (int i = 0; i < clienti.length; i++) {
//            clientiDTO[i] = getDTO(clienti[i]);
//        }
//        return clientiDTO;
//    }
//
//    public static Client[] getFromClientDTO(ClientDTO[] clientiDTO) {
//        Client[] clienti = new Client[clientiDTO.length];
//        for (int i = 0; i < clientiDTO.length; i++) {
//            clienti[i] = getFromClientDTO(clientiDTO[i]);
//        }
//        return clienti;
//    }
//
//    public static CursaDTO[] getDTO(Cursa[] curse) {
//        CursaDTO[] curseDTO = new CursaDTO[curse.length];
//        for (int i = 0; i < curse.length; i++) {
//            curseDTO[i] = getDTO(curse[i]);
//        }
//        return curseDTO;
//    }
//
//    public static Cursa[] getFromCursaDTO(CursaDTO[] curseDTO) {
//        Cursa[] curse = new Cursa[curseDTO.length];
//        for (int i = 0; i < curseDTO.length; i++) {
//            curse[i] = getFromCursaDTO(curseDTO[i]);
//        }
//        return curse;
//    }
//
//    public static RezervareDTO[] getDTO(Rezervare[] rezervari) {
//        RezervareDTO[] rezervariDTO = new RezervareDTO[rezervari.length];
//        for (int i = 0; i < rezervari.length; i++) {
//            rezervariDTO[i] = getDTO(rezervari[i]);
//        }
//        return rezervariDTO;
//    }
//
//    public static Rezervare[] getFromRezervareDTO(RezervareDTO[] rezervariDTO) {
//        Rezervare[] rezervari = new Rezervare[rezervariDTO.length];
//        for (int i = 0; i < rezervariDTO.length; i++) {
//            rezervari[i] = getFromRezervareDTO(rezervariDTO[i]);
//        }
//        return rezervari;
//    }
//



}
