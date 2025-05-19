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

import java.util.Collection;
import java.util.List;

public class AdminController implements IObserver {
    private IService service;
    private User adminUser;

    private final ObservableList<RentDTO> activeRents = FXCollections.observableArrayList();

    @FXML
    private ListView<RentDTO> rentListView;

    @FXML
    private Button returnButton;

    public void setService(IService service, User user) {
        this.service = service;
        this.adminUser = user;
        loadActiveRents();
    }

    private void loadActiveRents() {
        try {
            List<Rent> rents = (List<Rent>) service.getAllRents();
            List<RentDTO> active = rents.stream()
                    .filter(r -> r.getStatus().equals("ACTIVE"))
                    .map(DTOUtils::getDTO)
                    .toList();
            active.forEach(dto -> System.out.println("DTO final → id = " + dto.getId()));
            Platform.runLater(() -> {
                activeRents.setAll(active);
                rentListView.setItems(activeRents);
            });

        } catch (AppException e) {
            showError("Couldn't load rents: " + e.getMessage());
        }
    }

    @FXML
    private void handleReturn() {
        RentDTO selected = rentListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Selectează un împrumut.");
            return;
        }

        try {
            System.out.println("Selected RentDTO → id = " + selected.getId());
            service.returnBook(selected.getId());
            showInfo("Carte returnată cu succes.");
            loadActiveRents(); // Refresh listă

        } catch (AppException e) {
            showError("Returnarea a eșuat: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }

    private void showWarning(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).show();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }

    @Override
    public void updateBookCopies(Iterable<Integer> bookCopies) throws AppException
    {


    }

    @Override
    public void updateRents(Collection<Rent> rents) throws AppException {
        Platform.runLater(() -> {
            System.out.println("Updating rents in AdminController");
            activeRents.clear();
            List<RentDTO> active = rents.stream()
                    .filter(r -> r.getStatus().equals("ACTIVE"))
                    .map(DTOUtils::getDTO)
                    .toList();
            active.forEach(dto -> System.out.println("DTO final → id = " + dto.getId()));
            activeRents.setAll(active);
            rentListView.setItems(activeRents);
        });
    }
}
