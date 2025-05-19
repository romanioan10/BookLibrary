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
                User tempUser = new User(usernameField.getText(), passwordField.getText());

                try {
                    User loggedInUser = service.login(tempUser, null);

                    FXMLLoader loader;
                    Scene sceneToShow;
                    Stage nextStage = new Stage();

                    if (loggedInUser.getRole() == 0)
                    {
                        loader = new FXMLLoader(getClass().getResource("home.fxml"));
                        sceneToShow = new Scene(loader.load());

                        HomeController controller = loader.getController();

                        service.logout(loggedInUser);
                        loggedInUser = service.login(loggedInUser, controller);
                        controller.setService(service, loggedInUser);

                        nextStage.setTitle("Biblioteca Online - Cititor");

                    } else
                    {
                        loader = new FXMLLoader(getClass().getResource("admin_view.fxml"));
                        sceneToShow = new Scene(loader.load());

                        AdminController controller = loader.getController();
                        service.logout(loggedInUser);
                        loggedInUser = service.login(loggedInUser, controller);
                        controller.setService(service, loggedInUser);

                        nextStage.setTitle("Interfata Bibliotecar - Returnari");
                    }

                    primaryStage.close();
                    nextStage.setScene(sceneToShow);
                    nextStage.show();

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
