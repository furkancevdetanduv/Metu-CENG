package com.pisti.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GameStageInitializer implements ApplicationListener<PistiApplication.StageReadyEvent> {
    @Value("classpath:/loginPage.fxml") private Resource gameResource;
    @Value("${spring.application.ui.windowWidth}") private int windowWidth;
    @Value("${spring.application.ui.windowHeight}") private int windowHeight;
    private final String applicationTitle;
    private ApplicationContext applicationContext;

    /**
     * Initializes the game stage
     * @param applicationTitle Title of the application
     */
    public GameStageInitializer(@Value("${spring.application.ui.title}") String applicationTitle, ApplicationContext applicationContext) {
        this.applicationTitle = applicationTitle;
        this.applicationContext = applicationContext;
    }


    /**
     * Overrides the application event
     * @param stageReadyEvent Stage ready event to override
     */
    @Override
    public void onApplicationEvent(PistiApplication.StageReadyEvent stageReadyEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(gameResource.getURL());
            fxmlLoader.setControllerFactory(aClass -> applicationContext.getBean(aClass));
            Parent parent = fxmlLoader.load();

            Stage stage = stageReadyEvent.getStage();
            stage.setScene(new Scene(parent, windowWidth, windowHeight));
            stage.setMinHeight(600);
            stage.setMinWidth(800);
            stage.setTitle(applicationTitle);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
