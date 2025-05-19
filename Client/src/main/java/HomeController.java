import DTOs.BookCopyDTO;
import DTOs.RentDTO;
import Domain.BookCopy;
import Domain.Rent;
import Domain.User;
import Service.AppException;
import Service.IObserver;
import Service.IService;
import DTOs.DTOUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeController implements IObserver {

    private IService service;
    private User currentUser;

    private final ObservableList<BookCopyDTO> availableBooks = FXCollections.observableArrayList();
    private final ObservableList<RentDTO> userRents = FXCollections.observableArrayList();

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button rentButton;

    @FXML
    private ListView<BookCopyDTO> bibliotecaListView;

    @FXML
    private ListView<RentDTO> rentedBooksListView;

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label rentedCountLabel;


    public void initialize() {
        bibliotecaListView.setItems(availableBooks);
        rentedBooksListView.setItems(userRents);
    }

    public void setService(IService service, User user) {
        this.service = service;
        this.currentUser = user;

        nameLabel.setText("Name: " + user.getName());
        phoneLabel.setText("Phone: " + user.getPhone());
        roleLabel.setText("Role: " + (user.getRole() == 0 ? "Reader" : "Admin"));

        refreshBookCopies();
        refreshUserRents();
    }

    @FXML
    private void handleRent() {
        BookCopyDTO selected = bibliotecaListView.getSelectionModel().getSelectedItem();
        LocalDate selectedDate = datePicker.getValue();

        if (selected == null) {
            showWarning("Please select a book first.");
            return;
        }

        if (selectedDate == null) {
            showWarning("Please select a start date.");
            return;
        }

        System.out.println("=== HANDLE RENT START ===");
        System.out.println("Selected BookCopyDTO: " + selected);
        System.out.println("Selected start date: " + selectedDate);
        System.out.println("Current user ID: " + currentUser.getId());
        System.out.println("Current user name: " + currentUser.getName());

        new Thread(() -> {
            try {
                LocalDate endDate = selectedDate.plusDays(14);
                BookCopy bookCopy = DTOUtils.getFromBookCopyDTO(selected);

                System.out.println("Renting bookCopy ID: " + bookCopy.getId() + ", title: " + bookCopy.getBook().getTitle());
                System.out.println("Rent period: " + selectedDate + " -> " + endDate);
                System.out.println("Calling service.rentBook(...) with user ID: " + currentUser.getId());

                service.rentBook(currentUser, bookCopy, selectedDate, endDate, "ACTIVE");

                Platform.runLater(() -> {
                    showInfo("Book successfully rented!");
                    refreshUserRents();
                });

            } catch (Exception e) {
                Platform.runLater(() -> showError("Renting failed: " + e.getMessage()));
            }
        }).start();
    }



    // === Actualizare doar a cărților disponibile ===

    @Override
    public void updateBookCopies(Iterable<Integer> bookCopyIds) {
        Platform.runLater(() -> {
            try {
                List<BookCopyDTO> refreshed = new ArrayList<>();
                for (Integer id : bookCopyIds) {
                    BookCopy copy = service.getBookCopyById(id);  // Acum e sigur
                    if (copy != null && copy.getStatus() == BookCopy.Status.AVAILABLE) {
                        refreshed.add(DTOUtils.getDTO(copy));
                    }
                }

                System.out.println("Updating availableBooks list with " + refreshed.size() + " entries.");
                availableBooks.setAll(refreshed);

            } catch (AppException e) {
                showError("Failed to update book list: " + e.getMessage());
            }
        });
    }



    @Override
    public void updateRents(Collection<Rent> rents) throws AppException {
        Platform.runLater(() -> {
            List<RentDTO> userRentDTOs = rents.stream()
                    .filter(r -> r.getUser().getId() == currentUser.getId())
                    .map(DTOUtils::getDTO)
                    .toList();

            System.out.println("Refreshed rents via updateRents. Found " + userRentDTOs.size() + " entries.");
            userRents.setAll(userRentDTOs);
            rentedCountLabel.setText("Books rented: " + userRentDTOs.size());
        });
    }




    // === Doar local ===

    private void refreshUserRents() {
        try {
            Collection<Rent> allRents = (Collection<Rent>) service.getAllRents();
            System.out.println("Total rents in DB: " + allRents.size());
            System.out.println("Filtering rents for user ID = " + currentUser.getId());

            List<RentDTO> userRentDTOs = allRents.stream()
                    .filter(r -> r.getUser().getId() == currentUser.getId())
                    .map(DTOUtils::getDTO)
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                System.out.println("Found " + userRentDTOs.size() + " rents for current user.");
                userRents.setAll(userRentDTOs);
                rentedCountLabel.setText("Books rented: " + userRentDTOs.size());
            });

        } catch (AppException e) {
            showError("Could not load your rented books: " + e.getMessage());
        }
    }


    private void refreshBookCopies() {
        try {
            Iterable<Integer> ids = service.getAllBookCopies();
            List<BookCopyDTO> dtos = new ArrayList<>();

            for (Integer id : ids) {
                BookCopy copy = service.getBookCopyById(id);

                if (copy != null && copy.getStatus() == BookCopy.Status.AVAILABLE) {
                    dtos.add(DTOUtils.getDTO(copy));
                } else {
                    System.out.println("BookCopy ID = " + id + " is not AVAILABLE: " + (copy != null ? copy.getStatus() : "null"));
                }
            }

            Platform.runLater(() -> {
                availableBooks.setAll(dtos);
            });

        } catch (AppException e) {
            showError("Could not load available books: " + e.getMessage());
        }
    }

    // === Helpers ===

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.show();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.show();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.show();
    }
}
