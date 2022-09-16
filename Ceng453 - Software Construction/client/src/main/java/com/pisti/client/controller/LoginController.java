package com.pisti.client.controller;


import com.pisti.client.service.ClientService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import com.pisti.server.model.User;

import java.io.IOException;


/**
 * Controller that manages Login Page (Default first scene of the application)
 */
@Component
public class LoginController{

    private static final String Register_Page = "registerPage.fxml";
    private static final String level1_Page = "gameLevel1.fxml";

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    /**
     * Login method
     * Gets username and password from user and  logs in if user valid
     *
     * @param event button click event
     * @throws IOException input output exception
     */
    @FXML
    protected void loginHandle(ActionEvent event) throws IOException {
        if (!usernameField.getText().isEmpty()) {
            if (passwordField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please write your password!");
                alert.show();
                return;
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please write your username!");
            alert.show();
            return;
        }
        User user = User.builder().name(usernameField.getText()).password(passwordField.getText()).build();
        ClientService clientService = new ClientService();
        if(clientService.login(user)){
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(level1_Page));
            Parent dashboardPage = loader.load();
            Scene scene = new Scene(dashboardPage, 800, 600);
            currentStage.setScene(scene);
            currentStage.show();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Login failed, please try again!");
        alert.show();
    }

    /**
     * This method redirects user to register page.
     *
     * @param event button click event
     * @throws IOException input output exception
     */
    @FXML
    protected void redirectRegisterPage(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if(getClass().getClassLoader().getResource(Register_Page)!= null){
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(Register_Page));
            Parent registerPage = loader.load();
            Scene scene = new Scene(registerPage, 800, 600);
            currentStage.setScene(scene);
            currentStage.show();
        }
    }

    /**
     * This method lets user to skip login and redirects to first level.
     *
     * @param event button click event
     * @throws IOException input output exception
     */
    @FXML
    protected void skipLogin(ActionEvent event) throws IOException {
        Stage currentStage;
        currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (getClass().getClassLoader().getResource(level1_Page) == null) {
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(level1_Page));
        Parent level1Page = loader.load();
        Scene sceneLogin = new Scene(level1Page, 800, 600);
        currentStage.setScene(sceneLogin);
        currentStage.show();
    }

}

