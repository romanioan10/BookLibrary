import Domain.User;
import Service.AppException;
import Service.IService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginMain {
    private final IService service;

    public LoginMain(IService service) {
        this.service = service;
    }

    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        AnchorPane leftAnchorPane = new AnchorPane();
        leftAnchorPane.setPrefSize(200, 200);
        root.setLeft(leftAnchorPane);

        AnchorPane rightAnchorPane = new AnchorPane();
        rightAnchorPane.setPrefSize(200, 200);
        root.setRight(rightAnchorPane);

        AnchorPane centerAnchorPane = new AnchorPane();
        centerAnchorPane.setPrefSize(200, 200);
        root.setCenter(centerAnchorPane);

        Label usernameLabel = new Label("Username");
        usernameLabel.setLayoutX(14);
        usernameLabel.setLayoutY(153);
        usernameLabel.setFont(Font.font("Arial Narrow Bold", 16));
        centerAnchorPane.getChildren().add(usernameLabel);

        Label passwordLabel = new Label("Password");
        passwordLabel.setLayoutX(14);
        passwordLabel.setLayoutY(190);
        passwordLabel.setFont(Font.font("Arial Narrow Bold", 16));
        centerAnchorPane.getChildren().add(passwordLabel);

        TextField usernameField = new TextField();
        usernameField.setLayoutX(91);
        usernameField.setLayoutY(150);
        usernameField.setPrefSize(173, 26);
        centerAnchorPane.getChildren().add(usernameField);

        TextField passwordField = new TextField();
        passwordField.setLayoutX(91);
        passwordField.setLayoutY(186);
        passwordField.setPrefSize(173, 26);
        centerAnchorPane.getChildren().add(passwordField);

        Button loginButton = new Button("Login");
        loginButton.setLayoutX(16);
        loginButton.setLayoutY(295);
        loginButton.setPrefSize(231, 26);
        loginButton.setStyle("-fx-background-color: #fab025;");
        centerAnchorPane.getChildren().add(loginButton);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Form");
        primaryStage.show();

        loginButton.setOnAction(event -> {
            if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please fill in all fields");
                alert.showAndWait();
            } else {
                // 1. Creează user temporar cu doar username și parolă
                User user = new User(usernameField.getText(), passwordField.getText());

                try {
                    // 2. Încarcă FXML și controllerul
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                    Scene homeScene = new Scene(loader.load());

                    HomeController homeController = loader.getController();

                    // 3. Autentificare: trimite controllerul ca observer și primește user completat
                    user = service.login(user, homeController);  // <-- ID-ul și celelalte câmpuri sunt acum setate

                    // 4. Transmite user-ul corect în controller
                    homeController.setService(service, user);

                    // 5. Deschide aplicația principală
                    primaryStage.close();
                    Stage homeStage = new Stage();
                    homeStage.setScene(homeScene);
                    homeStage.setTitle("Biblioteca Online");
                    homeStage.show();

                } catch (AppException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid username or password");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
