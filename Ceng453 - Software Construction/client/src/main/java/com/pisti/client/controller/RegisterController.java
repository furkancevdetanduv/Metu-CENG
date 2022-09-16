package com.pisti.client.controller;

import com.pisti.client.service.ClientService;
import com.pisti.server.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class RegisterController {

    private static final String Login_Page = "loginPage.fxml";
    private static final String level1_Page = "gameLevel1.fxml";

    private final int PASSWORD_LENGTH = 6;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private ClientService clientService = new ClientService();

    /**
     * register method
     * Gets email, username and password , and creates an user.
     * @param event  button click event
     * @throws IOException input output exception
     * */
    public void registerHandle(ActionEvent event) throws IOException{
        if(emailField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please write your email!");
            alert.show();
            return;
        }
        else if(usernameField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please write your username!");
            alert.show();
            return;
        }
        else if(passwordField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please write your password!");
            alert.show();
            return;
        }
        else if(!emailField.getText().contains("@") || !emailField.getText().contains(".")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please write a valid email!");
            alert.show();
            return;
        }
        else if(passwordField.getText().length() < PASSWORD_LENGTH){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Your password must include at least six characters!");
            alert.show();
            return;
        }

        User user = User.builder().name(usernameField.getText()).password(passwordField.getText()).email(emailField.getText()).build();
        if(clientService.register(user)){
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent dashboardPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource(level1_Page)));
            Scene scene = new Scene(dashboardPage, 800, 600);
            currentStage.setScene(scene);
            currentStage.show();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Register failed, please try again!");
        alert.show();
    }

    /**
     * This method redirects user to login page.
     *
     * @param event button click event
     * @throws IOException input output exception
     */
    @FXML
    protected void redirectLoginPage(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent registerPage = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource(Login_Page)));
        Scene scene = new Scene(registerPage, 800, 600);
        currentStage.setScene(scene);
        currentStage.show();
    }
}
